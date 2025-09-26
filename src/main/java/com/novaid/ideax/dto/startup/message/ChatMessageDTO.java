package com.novaid.ideax.dto.startup.message;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDTO {
    private Long senderId;
    private Long receiverId;
    private String content;
}
