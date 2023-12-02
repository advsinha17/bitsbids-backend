package com.bitsbids.bitsbids.Messages;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {
    private String sessionId;
    private UUID senderAnonId;
    private UUID recipientAnonId;
    private String content;
    private String timestamp;
}