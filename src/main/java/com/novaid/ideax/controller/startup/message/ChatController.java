package com.novaid.ideax.controller.startup.message;

import com.novaid.ideax.dto.startup.message.ChatMessageDTO;
import com.novaid.ideax.entity.startup.message.ChatMessage;
import com.novaid.ideax.service.startup.message.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    // WebSocket: gửi tin nhắn
    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(ChatMessageDTO dto) {
        try {
            return chatService.saveMessage(dto);
        } catch (RuntimeException e) {
            // Có thể trả về 1 message đặc biệt để FE hiểu
            return ChatMessage.builder()
                    .content("❌ " + e.getMessage())
                    .timestamp(java.time.LocalDateTime.now())
                    .build();
        }
    }


    // REST: lấy lịch sử chat
    @GetMapping("/history/{senderId}/{receiverId}")
    public List<ChatMessage> getChatHistory(
            @PathVariable Long senderId,
            @PathVariable Long receiverId) {
        return chatService.getChatHistory(senderId, receiverId);
    }
}
