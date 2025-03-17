package com.example.quizchat.controller;

import com.example.quizchat.model.ChatMessage;
import com.example.quizchat.model.ChatRoom;
import com.example.quizchat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // REST endpoints
    @GetMapping("/api/chat/rooms")
    public ResponseEntity<List<ChatRoom>> getAllActiveRooms() {
        return ResponseEntity.ok(chatService.getAllActiveRooms());
    }

    @GetMapping("/api/chat/rooms/{id}")
    public ResponseEntity<ChatRoom> getRoomById(@PathVariable Long id) {
        return chatService.getRoomById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/chat/rooms")
    public ResponseEntity<ChatRoom> createRoom(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String description = request.get("description");
        Long createdById = Long.valueOf(request.get("createdById"));

        ChatRoom room = chatService.createRoom(name, description, createdById);
        return ResponseEntity.ok(room);
    }

    @DeleteMapping("/api/chat/rooms/{id}")
    public ResponseEntity<?> deactivateRoom(@PathVariable Long id) {
        chatService.deactivateRoom(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/chat/messages/{roomId}")
    public ResponseEntity<List<ChatMessage>> getMessagesByRoomId(@PathVariable Long roomId) {
        return ResponseEntity.ok(chatService.getMessagesByRoomId(roomId));
    }

    @GetMapping("/api/chat/messages/recent/{roomId}")
    public ResponseEntity<List<ChatMessage>> getRecentMessagesByRoomId(@PathVariable Long roomId) {
        return ResponseEntity.ok(chatService.getRecentMessagesByRoomId(roomId));
    }

    @PostMapping("/api/chat/rooms/{roomId}/join")
    public ResponseEntity<?> joinRoom(@PathVariable Long roomId, @RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        chatService.joinRoom(roomId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/chat/rooms/{roomId}/leave")
    public ResponseEntity<?> leaveRoom(@PathVariable Long roomId, @RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        chatService.leaveRoom(roomId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/chat/rooms/{roomId}/mute")
    public ResponseEntity<?> muteUser(@PathVariable Long roomId, @RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        Long adminId = request.get("adminId");
        chatService.muteUser(roomId, userId, adminId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/chat/rooms/{roomId}/unmute")
    public ResponseEntity<?> unmuteUser(@PathVariable Long roomId, @RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        Long adminId = request.get("adminId");
        chatService.unmuteUser(roomId, userId, adminId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/chat/rooms/{roomId}/kick")
    public ResponseEntity<?> kickUser(@PathVariable Long roomId, @RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        Long adminId = request.get("adminId");
        chatService.kickUser(roomId, userId, adminId);
        return ResponseEntity.ok().build();
    }

    // WebSocket endpoints
    @MessageMapping("/chat.sendMessage/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public ChatMessage sendMessage(@DestinationVariable Long roomId, @Payload Map<String, Object> messageRequest) {
        Long userId = Long.valueOf(messageRequest.get("userId").toString());
        String messageText = messageRequest.get("message").toString();

        return chatService.saveMessage(roomId, userId, messageText);
    }

    @MessageMapping("/chat.join/{roomId}")
    @SendTo("/topic/room/{roomId}/users")
    public String joinRoomWebSocket(@DestinationVariable Long roomId, @Payload Map<String, Long> joinRequest) {
        Long userId = joinRequest.get("userId");
        chatService.joinRoom(roomId, userId);
        return "User " + userId + " has joined the room";
    }

    @MessageMapping("/chat.leave/{roomId}")
    @SendTo("/topic/room/{roomId}/users")
    public String leaveRoomWebSocket(@DestinationVariable Long roomId, @Payload Map<String, Long> leaveRequest) {
        Long userId = leaveRequest.get("userId");
        chatService.leaveRoom(roomId, userId);
        return "User " + userId + " has left the room";
    }
}
