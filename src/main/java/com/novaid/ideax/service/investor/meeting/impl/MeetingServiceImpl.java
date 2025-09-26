package com.novaid.ideax.service.investor.meeting.impl;

import com.novaid.ideax.config.JitsiJwtUtil;
import com.novaid.ideax.dto.investor.meet.MeetingRoomDTO;
import com.novaid.ideax.entity.auth.Account;
import com.novaid.ideax.entity.investor.meet.MeetingRoom;

import com.novaid.ideax.entity.investor.nda.NdaAgreement;
import com.novaid.ideax.entity.investor.nda.NdaTemplate;
import com.novaid.ideax.repository.auth.AccountRepository;
import com.novaid.ideax.repository.investor.meet.MeetingRoomRepository;

import com.novaid.ideax.repository.investor.nda.NdaAgreementRepository;
import com.novaid.ideax.repository.investor.nda.NdaTemplateRepository;
import com.novaid.ideax.service.investor.meeting.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRoomRepository meetingRoomRepository;
    private final NdaAgreementRepository ndaAgreementRepository;
    private final NdaTemplateRepository ndaTemplateRepository;
    private final AccountRepository accountRepository;
    private final JitsiJwtUtil jitsiJwtUtil;
    @Override
    public MeetingRoomDTO createMeeting(MeetingRoomDTO dto) {
        Account investor = accountRepository.findById(dto.getCreatedById())
                .orElseThrow(() -> new RuntimeException("Investor not found"));

        MeetingRoom room = MeetingRoom.builder()
                .roomCode("ROOM-" + System.currentTimeMillis())
                .topic(dto.getTopic())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .createdBy(investor)
                .build();

        MeetingRoom saved = meetingRoomRepository.save(room);
        return mapMeetingToDTO(saved);
    }

    @Override
    public List<MeetingRoomDTO> getAllMeetings() {
        return meetingRoomRepository.findAll()
                .stream().map(this::mapMeetingToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public String joinMeeting(Long meetingId, Long userId) {
        // lấy NDA global mới nhất
        NdaTemplate latestTemplate = ndaTemplateRepository.findAll().stream()
                .reduce((first, second) -> second) // lấy bản cuối cùng
                .orElseThrow(() -> new RuntimeException("No NDA template found"));

        // check đã ký chưa
        boolean signed = ndaAgreementRepository.findByUserIdAndNdaTemplateId(userId, latestTemplate.getId())
                .map(NdaAgreement::isSigned)
                .orElse(false);

        if (!signed) {
            throw new RuntimeException("User must sign NDA before joining meeting");
        }

        // tìm meeting
        MeetingRoom room = meetingRoomRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Meeting not found"));

        // tạo JWT và meeting URL
        Account user = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String jwt = jitsiJwtUtil.generateJwt(room.getRoomCode(), user.getEmail());
        return jitsiJwtUtil.buildMeetingUrl(room.getRoomCode(), jwt);
    }

    @Override
    public MeetingRoomDTO getMeeting(Long id) {
        return meetingRoomRepository.findById(id)
                .map(this::mapMeetingToDTO)
                .orElseThrow(() -> new RuntimeException("Meeting not found"));
    }

    @Override
    public void updateRecording(Long meetingId, String recordUrl) {
        MeetingRoom room = meetingRoomRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Meeting not found"));
        room.setRecordUrl(recordUrl);
        meetingRoomRepository.save(room);
    }

    /**
     * Check user đã ký NDA global chưa để join meeting
     */
    @Override
    public boolean canJoinMeeting(Long meetingId, Long userId) {
        // lấy NDA mới nhất (giả sử dùng bản global cuối cùng admin upload)
        NdaTemplate latestTemplate = ndaTemplateRepository.findAll().stream()
                .reduce((first, second) -> second) // lấy phần tử cuối cùng
                .orElseThrow(() -> new RuntimeException("No NDA template found"));

        return ndaAgreementRepository.findByUserIdAndNdaTemplateId(userId, latestTemplate.getId())
                .map(NdaAgreement::isSigned)
                .orElse(false);
    }

    private MeetingRoomDTO mapMeetingToDTO(MeetingRoom room) {
        return MeetingRoomDTO.builder()
                .id(room.getId())
                .roomCode(room.getRoomCode())
                .topic(room.getTopic())
                .startTime(room.getStartTime())
                .endTime(room.getEndTime())
                .createdById(room.getCreatedBy().getId())
                .recordUrl(room.getRecordUrl())
                .build();
    }
}
