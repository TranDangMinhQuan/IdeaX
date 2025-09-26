package com.novaid.ideax.controller.investor.meet;

import com.novaid.ideax.dto.investor.meet.MeetingRoomDTO;
import com.novaid.ideax.service.investor.meeting.MeetingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meetings")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    // ✅ Chỉ ADMIN và INVESTOR được phép tạo meeting
    @PreAuthorize("hasAnyRole('ADMIN','INVESTOR')")
    @PostMapping
    public ResponseEntity<MeetingRoomDTO> createMeeting(@RequestBody MeetingRoomDTO dto) {
        return ResponseEntity.ok(meetingService.createMeeting(dto));
    }

    // ✅ Chỉ ADMIN được lấy danh sách tất cả meetings
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<MeetingRoomDTO>> getAll() {
        return ResponseEntity.ok(meetingService.getAllMeetings());
    }
    // ✅ User join meeting
    // ✅ User join meeting (chỉ khi đã ký NDA global)
    @PreAuthorize("hasAnyRole('INVESTOR','STARTUP')")
    @GetMapping("/{id}/join/{userId}")
    public ResponseEntity<String> joinMeeting(@PathVariable Long id, @PathVariable Long userId) {
        String url = meetingService.joinMeeting(id, userId);
        return ResponseEntity.ok(url);
    }


    // ✅ ADMIN hoặc người tạo meeting (INVESTOR) mới được xem chi tiết
    @PreAuthorize("hasAnyRole('ADMIN','INVESTOR','STARTUP')")
    @GetMapping("/{id}")
    public ResponseEntity<MeetingRoomDTO> getMeeting(@PathVariable Long id) {
        return ResponseEntity.ok(meetingService.getMeeting(id));
    }

    // ✅ Chỉ ADMIN mới được update record
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/record")
    public ResponseEntity<Void> updateRecording(@PathVariable Long id, @RequestParam String recordUrl) {
        meetingService.updateRecording(id, recordUrl);
        return ResponseEntity.ok().build();
    }

    // ✅ Check user có quyền join hay không (Investor/Startup)
    @PreAuthorize("hasAnyRole('INVESTOR','STARTUP')")
    @GetMapping("/{id}/can-join/{userId}")
    public ResponseEntity<Boolean> canJoin(@PathVariable Long id, @PathVariable Long userId) {
        return ResponseEntity.ok(meetingService.canJoinMeeting(id, userId));
    }
}
