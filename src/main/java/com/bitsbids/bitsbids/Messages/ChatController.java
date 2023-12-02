package com.bitsbids.bitsbids.Messages;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
@CrossOrigin
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/messages/session/{sessionId}")
    public ResponseEntity<List<ChatMessage>> getMessagesForSession(@PathVariable String sessionId) {
        return ResponseEntity.ok(chatService.getMessagesForSession(sessionId));
    }

    @PostMapping("/message")
    public ResponseEntity<ChatMessage> addMessage(@RequestBody ChatMessage message) {
        ChatMessage savedMessage = chatService.saveMessage(message);
        return ResponseEntity.ok(savedMessage);
    }

}
