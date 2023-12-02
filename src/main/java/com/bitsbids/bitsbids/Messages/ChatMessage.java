package com.bitsbids.bitsbids.Messages;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import com.bitsbids.bitsbids.AnonymousUser.AnonymousUser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ChatMessage {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "chat_message_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @ManyToOne
    @JoinColumn(name = "sender_anon_id", referencedColumnName = "anon_user_id")
    private AnonymousUser sender;

    @ManyToOne
    @JoinColumn(name = "recipient_anon_id", referencedColumnName = "anon_user_id")
    private AnonymousUser recipient;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;
}
