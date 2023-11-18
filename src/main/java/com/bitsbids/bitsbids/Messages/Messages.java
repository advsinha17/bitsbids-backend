package com.bitsbids.bitsbids.Messages;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor

@Table(name = "bids", uniqueConstraints = {
    @UniqueConstraint(name= "bid_id", columnNames = {"bid_id"}),
    @UniqueConstraint(name= "conversation_id", columnNames = {"conversation_id"}),
    @UniqueConstraint(name= "sender_anonymous_id", columnNames = {"sender_anonymous_id"}),
})

public class Messages {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")

    @Column(name = "message_id", nullable = false, updatable = false)
    private UUID messageID;

    @Column(name = "conversation_id", nullable = false)
    private UUID username;

    @Column(name = "sender_anonymous_id", nullable = false)
    private UUID senderAnonymousID;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    @Column(name = "attachments", nullable = false)
    private String attachments; 

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
}
