package com.novaid.ideax.repository.message;


import com.novaid.ideax.entity.startup.message.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySenderIdAndReceiverIdOrReceiverIdAndSenderId(
            Long senderId, Long receiverId,
            Long receiverId2, Long senderId2
    );
}