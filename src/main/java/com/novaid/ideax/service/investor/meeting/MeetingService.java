package com.novaid.ideax.service.investor.meeting;

import com.novaid.ideax.dto.investor.meet.MeetingRoomDTO;

import java.util.List;

public interface MeetingService {
    MeetingRoomDTO createMeeting(MeetingRoomDTO dto);
    List<MeetingRoomDTO> getAllMeetings();
    MeetingRoomDTO getMeeting(Long id);
    void updateRecording(Long meetingId, String recordUrl);
    boolean canJoinMeeting(Long meetingId, Long userId);
    String joinMeeting(Long meetingId, Long userId);
}
