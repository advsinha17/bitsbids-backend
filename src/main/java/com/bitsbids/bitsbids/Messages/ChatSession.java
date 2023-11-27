package com.bitsbids.bitsbids.Messages;

import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ChatSession {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "chat_session_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "buyer_anon_id", nullable = false)
    private UUID buyerAnonId;

    @Column(name = "seller_anon_id", nullable = false)
    private UUID sellerAnonId;
}
