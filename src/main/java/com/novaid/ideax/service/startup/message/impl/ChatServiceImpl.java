package com.novaid.ideax.service.startup.message.impl;


import com.novaid.ideax.dto.startup.message.ChatMessageDTO;
import com.novaid.ideax.entity.auth.Account;

import com.novaid.ideax.entity.startup.message.ChatMessage;
import com.novaid.ideax.repository.auth.AccountRepository;

import com.novaid.ideax.repository.startup.message.ChatMessageRepository;
import com.novaid.ideax.service.startup.message.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final AccountRepository accountRepository;

    @Override
    public ChatMessage saveMessage(ChatMessageDTO dto) {
        Account sender = accountRepository.findById(dto.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        Account receiver = accountRepository.findById(dto.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        // 🔒 Check phân quyền: chỉ cho phép Startup ↔ Investor
        if (sender.getRole() == receiver.getRole()) {
            throw new RuntimeException("Chat not allowed between same role: " + sender.getRole());
        }

        ChatMessage message = ChatMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .content(dto.getContent())
                .timestamp(LocalDateTime.now())
                .build();

        return chatMessageRepository.save(message);
    }

    @Override
    public List<ChatMessage> getChatHistory(Long senderId, Long receiverId) {
        return chatMessageRepository.findBySenderIdAndReceiverIdOrReceiverIdAndSenderId(
                senderId, receiverId,
                senderId, receiverId
        );
    }
}
