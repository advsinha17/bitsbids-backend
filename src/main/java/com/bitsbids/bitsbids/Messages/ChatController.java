package com.bitsbids.bitsbids.Messages;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
@CrossOrigin
public class ChatController {

    @Autowired
    private ChatService chatService;

    // Get all chat sessions for a real user
    @GetMapping("/sessions/user/{userId}")
    public ResponseEntity<List<ChatSession>> getChatSessionsForUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(chatService.getAllChatSessionsForUser(userId));
    }

    // Get all messages for a specific session
    @GetMapping("/messages/session/{sessionId}")
    public ResponseEntity<List<ChatMessage>> getMessagesForSession(@PathVariable String sessionId) {
        return ResponseEntity.ok(chatService.getMessagesForSession(sessionId));
    }

    // Add a new message to a chat session
    @PostMapping("/message")
    public ResponseEntity<ChatMessage> addMessage(@RequestBody ChatMessage message) {
        ChatMessage savedMessage = chatService.saveMessage(message);
        return ResponseEntity.ok(savedMessage);
    }

    // Create a new chat session
    @PostMapping("/session")
    public ResponseEntity<ChatSession> createChatSession(@RequestBody ChatSession chatSession) {
        ChatSession newSession = chatService.createChatSession(chatSession.getBuyerAnonId(),
                chatSession.getSellerAnonId());
        return ResponseEntity.ok(newSession);
    }
}
