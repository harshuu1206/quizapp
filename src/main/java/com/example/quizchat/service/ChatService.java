package com.example.quizchat.service;


import com.example.quizchat.model.ChatMessage;
import com.example.quizchat.model.ChatRoom;
import com.example.quizchat.model.User;
import com.example.quizchat.repository.ChatMessageRepository;
import com.example.quizchat.repository.ChatRoomRepository;
import com.example.quizchat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ChatService(
            ChatRoomRepository chatRoomRepository,
            ChatMessageRepository chatMessageRepository,
            UserRepository userRepository,
            SimpMessagingTemplate messagingTemplate) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public List<ChatRoom> getAllActiveRooms() {
        return chatRoomRepository.findByIsActiveTrue();
    }

    public Optional<ChatRoom> getRoomById(Long id) {
        return chatRoomRepository.findById(id);
    }

    @Transactional
    public ChatRoom createRoom(String name, String description, Long createdById) {
        User createdBy = userRepository.findById(createdById)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(name);
        chatRoom.setDescription(description);
        chatRoom.setCreatedBy(createdBy);
        chatRoom.setActive(true);

        return chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public void deactivateRoom(Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        room.setActive(false);
        chatRoomRepository.save(room);

        // Notify all users in the room
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/status", "Room has been deactivated");
    }

    @Transactional
    public ChatMessage saveMessage(Long roomId, Long userId, String messageText) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChatMessage message = new ChatMessage();
        message.setRoom(room);
        message.setUser(user);
        message.setMessageText(messageText);

        ChatMessage savedMessage = chatMessageRepository.save(message);

        // Send message to WebSocket subscribers
        messagingTemplate.convertAndSend("/topic/room/" + roomId, savedMessage);

        return savedMessage;
    }

    public List<ChatMessage> getMessagesByRoomId(Long roomId) {
        return chatMessageRepository.findByRoomIdOrderByCreatedAt(roomId);
    }

    public List<ChatMessage> getRecentMessagesByRoomId(Long roomId) {
        return chatMessageRepository.findTop50ByRoomIdOrderByCreatedAtDesc(roomId);
    }

    @Transactional
    public void joinRoom(Long roomId, Long userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<User> users = room.getUsers();
        users.add(user);
        room.setUsers(users);

        chatRoomRepository.save(room);

        // Notify room about new user
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/users",
                "User " + user.getUsername() + " has joined the room");
    }

    @Transactional
    public void leaveRoom(Long roomId, Long userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<User> users = room.getUsers();
        users.remove(user);
        room.setUsers(users);

        chatRoomRepository.save(room);

        // Notify room about user leaving
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/users",
                "User " + user.getUsername() + " has left the room");
    }

    @Transactional
    public void muteUser(Long roomId, Long userId, Long adminId) {
        // Implementation for muting a user in a room
        // This would typically involve updating a join table or a flag in the database

        // Notify the user that they've been muted
        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/notifications",
                "You have been muted in room " + roomId);
    }

    @Transactional
    public void unmuteUser(Long roomId, Long userId, Long adminId) {
        // Implementation for unmuting a user in a room

        // Notify the user that they've been unmuted
        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/notifications",
                "You have been unmuted in room " + roomId);
    }

    @Transactional
    public void kickUser(Long roomId, Long userId, Long adminId) {
        leaveRoom(roomId, userId);

        // Notify the user that they've been kicked
        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/notifications",
                "You have been kicked from room " + roomId);
    }
}
