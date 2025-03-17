package com.example.quizchat.repository;

import com.example.quizchat.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByRoomIdOrderByCreatedAt(Long roomId);
    List<ChatMessage> findTop50ByRoomIdOrderByCreatedAtDesc(Long roomId);
}
