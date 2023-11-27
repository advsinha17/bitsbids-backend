package com.bitsbids.bitsbids.Messages;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    List<ChatMessage> findBySessionId(String sessionId);

    List<ChatMessage> findBySessionIdIn(Set<UUID> sessionIds);
}