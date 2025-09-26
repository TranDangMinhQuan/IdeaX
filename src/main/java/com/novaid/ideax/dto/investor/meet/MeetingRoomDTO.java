package com.novaid.ideax.dto.investor.meet;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingRoomDTO {
    private Long id;
    private String roomCode;
    private String topic;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long createdById;
    private String recordUrl;
}
