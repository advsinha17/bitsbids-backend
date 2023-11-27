package com.bitsbids.bitsbids.Messages;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bitsbids.bitsbids.AnonymousUser.AnonymousUserService;

@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private AnonymousUserService anonymousUserService;

    public ChatMessage saveMessage(ChatMessage message) {
        return chatMessageRepository.save(message);
    }

    public List<ChatMessage> getMessagesForSession(String sessionId) {
        return chatMessageRepository.findBySessionId(sessionId);
    }

    public List<ChatSession> getChatSessionsForBuyer(UUID buyerId) {
        return chatSessionRepository.findByBuyerAnonId(buyerId);
    }

    public List<ChatSession> getChatSessionsForSeller(UUID sellerId) {
        return chatSessionRepository.findBySellerAnonId(sellerId);
    }

    public List<ChatMessage> getAllMessagesForUser(UUID realUserId) {
        List<UUID> anonIds = getAnonIdsForRealUser(realUserId);

        Set<UUID> sessionIds = anonIds.stream()
                .flatMap(anonId -> Stream.concat(
                        chatSessionRepository.findByBuyerAnonId(anonId).stream(),
                        chatSessionRepository.findBySellerAnonId(anonId).stream()))
                .map(ChatSession::getId)
                .collect(Collectors.toSet());

        return chatMessageRepository.findBySessionIdIn(sessionIds);
    }

    public List<ChatSession> getAllChatSessionsForUser(UUID realUserId) {
        List<UUID> anonIds = anonymousUserService.getAnonIdsByRealUserId(realUserId);

        Set<ChatSession> allSessions = new HashSet<>();
        for (UUID anonId : anonIds) {
            allSessions.addAll(chatSessionRepository.findByBuyerAnonId(anonId));
            allSessions.addAll(chatSessionRepository.findBySellerAnonId(anonId));
        }

        return new ArrayList<>(allSessions);
    }

    private List<UUID> getAnonIdsForRealUser(UUID realUserId) {
        return anonymousUserService.getAnonIdsByRealUserId(realUserId);
    }

    public ChatSession createChatSession(UUID buyerId, UUID sellerId) {
        ChatSession newSession = new ChatSession();
        newSession.setBuyerAnonId(buyerId);
        newSession.setSellerAnonId(sellerId);
        return chatSessionRepository.save(newSession);
    }
}
