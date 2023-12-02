package com.bitsbids.bitsbids.Messages;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.bitsbids.bitsbids.AnonymousUser.AnonymousUser;
import com.bitsbids.bitsbids.AnonymousUser.AnonymousUserService;

@Controller
public class WebSocketChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessageSendingOperations simpMessageSendingOperations;

    @Autowired
    private AnonymousUserService anonymousUserService;

    @MessageMapping("/chat.sendMessage/{sessionId}")
    public void sendMessage(@DestinationVariable UUID sessionId, @Payload ChatMessageDTO chatMessageDTO) {
        ChatMessage chatMessage = convertToEntity(chatMessageDTO);

        chatService.saveMessage(chatMessage);
        simpMessageSendingOperations.convertAndSend(("/topic/" + sessionId), chatMessage);

    }

    private ChatMessage convertToEntity(ChatMessageDTO chatMessageDTO) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSessionId(chatMessageDTO.getSessionId());
        chatMessage.setContent(chatMessageDTO.getContent());

        Instant instant = Instant.parse(chatMessageDTO.getTimestamp());
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        chatMessage.setTimestamp(dateTime);

        AnonymousUser sender = anonymousUserService.getAnonUserById(chatMessageDTO.getSenderAnonId()).orElse(null);
        AnonymousUser recipient = anonymousUserService.getAnonUserById(chatMessageDTO.getRecipientAnonId())
                .orElse(null);
        chatMessage.setSender(sender);
        chatMessage.setRecipient(recipient);

        return chatMessage;
    }

    @GetMapping("/chat/sessions/user/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getAllSessionsForUser(@PathVariable UUID userId) {
        try {
            List<Map<String, Object>> sessions = chatService.getAllChatSessionsForUser(userId);
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/chat/startSession")
    public ResponseEntity<?> startSession(@RequestBody StartSessionRequest request) {
        try {
            UUID buyerId = request.getBuyerId();
            UUID sellerId = request.getSellerId();
            UUID productId = request.getProductId();

            ChatSession session = chatService.startChatSessionWithProductAndUsers(buyerId, sellerId, productId);
            return ResponseEntity.ok(session.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public static class StartSessionRequest {
        private UUID buyerId;
        private UUID sellerId;
        private UUID productId;

        public UUID getBuyerId() {
            return buyerId;
        }

        public void setBuyerId(UUID buyerId) {
            this.buyerId = buyerId;
        }

        public UUID getSellerId() {
            return sellerId;
        }

        public void setSellerId(UUID sellerId) {
            this.sellerId = sellerId;
        }

        public UUID getProductId() {
            return productId;
        }

        public void setProductId(UUID productId) {
            this.productId = productId;
        }

        @Override
        public String toString() {
            return "StartSessionRequest{" +
                    "buyerId=" + buyerId +
                    ", sellerId=" + sellerId +
                    ", productId=" + productId +
                    '}';
        }
    }

    @PostMapping("/chat/markMessageAsRead/{messageId}")
    public ResponseEntity<?> markMessageAsRead(@PathVariable UUID messageId) {
        try {
            chatService.markMessageAsRead(messageId);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/chat/unreadCount/{sessionId}/{userId}")
    public ResponseEntity<Integer> getUnreadMessagesCount(
            @PathVariable String sessionId,
            @PathVariable UUID userId) {

        try {
            int count = chatService.countUnreadMessages(sessionId, userId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
