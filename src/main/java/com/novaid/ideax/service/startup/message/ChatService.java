package com.novaid.ideax.service.startup.message;


import com.novaid.ideax.dto.startup.message.ChatMessageDTO;
import com.novaid.ideax.entity.startup.message.ChatMessage;

import java.util.List;

public interface ChatService {
    ChatMessage saveMessage(ChatMessageDTO dto);
    List<ChatMessage> getChatHistory(Long senderId, Long receiverId);
}