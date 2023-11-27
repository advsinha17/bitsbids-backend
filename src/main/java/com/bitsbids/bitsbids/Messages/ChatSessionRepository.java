package com.bitsbids.bitsbids.Messages;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {

    List<ChatSession> findByBuyerAnonId(UUID buyerId);

    List<ChatSession> findBySellerAnonId(UUID sellerId);

}