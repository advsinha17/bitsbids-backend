package com.bitsbids.bitsbids.Messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bitsbids.bitsbids.AnonymousUser.AnonymousUser;
import com.bitsbids.bitsbids.AnonymousUser.AnonymousUserRepository;
import com.bitsbids.bitsbids.AnonymousUser.AnonymousUserService;
import com.bitsbids.bitsbids.Bids.Bids;
import com.bitsbids.bitsbids.Product.Product;
import com.bitsbids.bitsbids.Product.ProductService;
import com.bitsbids.bitsbids.Product.Product.ProductStatus;
import com.bitsbids.bitsbids.Users.User;
import com.bitsbids.bitsbids.Users.UserService;

@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private AnonymousUserService anonymousUserService;

    @Autowired
    private AnonymousUserRepository anonymousUserRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    public ChatSession startChatSessionWithProductAndUsers(UUID realBuyerId, UUID realSellerId, UUID productId) {
        AnonymousUser buyerAnonUser = findOrCreateAnonUser(realBuyerId, productId, AnonymousUser.UserRole.CHATTER);
        AnonymousUser sellerAnonUser = findOrCreateAnonUser(realSellerId, productId, AnonymousUser.UserRole.SELLER);
        Optional<ChatSession> existingSession = chatSessionRepository
                .findByBuyerAnonIdAndSellerAnonId(buyerAnonUser.getAnonUserId(), sellerAnonUser.getAnonUserId());

        if (existingSession.isPresent()) {
            return existingSession.get();
        } else {
            return createChatSession(buyerAnonUser.getAnonUserId(), sellerAnonUser.getAnonUserId());
        }
    }

    private AnonymousUser findOrCreateAnonUser(UUID realUserId, UUID productId, AnonymousUser.UserRole role) {
        Optional<AnonymousUser> existingAnonUser = anonymousUserService.findAnonUserForUserAndProduct(realUserId,
                productId, role);

        if (existingAnonUser.isPresent()) {

            return existingAnonUser.get();
        } else {
            if (role == AnonymousUser.UserRole.CHATTER) {
                Optional<AnonymousUser> exOptional = anonymousUserService.findAnonUserForUserAndProduct(realUserId,
                        productId, AnonymousUser.UserRole.BIDDER);
                if (exOptional.isPresent()) {
                    return exOptional.get();
                }
            }
            AnonymousUser newAnonUser = anonymousUserService.addAnonymousUser(realUserId, role);
            Optional<Product> optProduct = productService.getProductById(productId);
            if (!optProduct.isPresent()) {
                throw new IllegalStateException("Product not found with ID: " + productId);
            }

            newAnonUser.setProduct(optProduct.get());

            return anonymousUserRepository.save(newAnonUser);
        }
    }

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

    public List<Map<String, Object>> getAllChatSessionsForUser(UUID realUserId) {
        List<AnonymousUser> userAnonProfiles = anonymousUserService.getAnonUserByUserId(realUserId);
        Set<ChatSession> allSessions = new HashSet<>();

        for (AnonymousUser anonUser : userAnonProfiles) {
            allSessions.addAll(chatSessionRepository.findByBuyerAnonId(anonUser.getAnonUserId()));
            allSessions.addAll(chatSessionRepository.findBySellerAnonId(anonUser.getAnonUserId()));
        }

        List<Map<String, Object>> sessionDetails = new ArrayList<>();
        for (ChatSession session : allSessions) {
            Map<String, Object> sessionDetail = new HashMap<>();
            sessionDetail.put("id", session.getId());

            Product product = getProductForAnonId(session.getBuyerAnonId(), session.getSellerAnonId());
            boolean isProductSold = product != null && product.getProductStatus() == ProductStatus.SOLD;

            // Add actual and anonymous IDs
            sessionDetail.put("actualBuyerId", getActualUserId(session.getBuyerAnonId()));
            sessionDetail.put("actualSellerId", getActualUserId(session.getSellerAnonId()));
            sessionDetail.put("anonBuyerId", session.getBuyerAnonId());
            sessionDetail.put("anonSellerId", session.getSellerAnonId());
            sessionDetail.put("isProductSold", isProductSold);

            // Add product details
            if (product != null) {

                sessionDetail.put("productId", product.getProductId());
                sessionDetail.put("latestBid", product.getLatestBid());
                sessionDetail.put("productStatus", product.getProductStatus());
                // Add other product fields as needed
            }

            // Determine which username to display
            String receiverUsername;
            if (isProductSold && isLatestBidderMatching(session, product)) {
                UUID actualBuyerId = (UUID) sessionDetail.get("actualBuyerId");
                UUID actualSellerId = (UUID) sessionDetail.get("actualSellerId");
                receiverUsername = getRealUsername(session, realUserId, actualBuyerId, actualSellerId);
            } else {
                UUID anonBuyerId = (UUID) sessionDetail.get("anonBuyerId");
                UUID anonSellerId = (UUID) sessionDetail.get("anonSellerId");
                receiverUsername = getAnonUsername(session, realUserId, anonBuyerId, anonSellerId);
            }
            sessionDetail.put("receiverUsername", receiverUsername);

            ChatMessage latestMessage = chatMessageRepository
                    .findFirstBySessionIdOrderByTimestampDesc(session.getId().toString());
            if (latestMessage != null) {
                sessionDetail.put("latestMessage", latestMessage.getContent());
                sessionDetail.put("latestMessageTimestamp", latestMessage.getTimestamp());
            }

            sessionDetails.add(sessionDetail);
        }
        return sessionDetails;
    }

    private String getAnonUsername(ChatSession session, UUID realUserId, UUID buyerAnonId, UUID sellerAnonId) {
        UUID otherPartyAnonId = buyerAnonId;
        if (anonymousUserService.getAnonUserById(buyerAnonId).map(AnonymousUser::getUser).map(User::getUserId)
                .orElse(null).equals(realUserId)) {
            otherPartyAnonId = sellerAnonId;
        }

        return anonymousUserService.getAnonUserById(otherPartyAnonId)
                .map(AnonymousUser::getAnonUsername)
                .orElse("Anonymous");
    }

    private String getRealUsername(ChatSession session, UUID realUserId, UUID actualBuyerId, UUID actualSellerId) {
        UUID otherPartyRealId = actualBuyerId.equals(realUserId) ? actualSellerId : actualBuyerId;

        return userService.getUserbyId(otherPartyRealId)
                .map(user -> user.getUsername())
                .orElse("User");
    }

    private Product getProductForAnonId(UUID buyerAnonId, UUID sellerAnonId) {
        Optional<AnonymousUser> buyerAnonUser = anonymousUserService.getAnonUserById(buyerAnonId);
        Optional<AnonymousUser> sellerAnonUser = anonymousUserService.getAnonUserById(sellerAnonId);

        if (buyerAnonUser.isPresent() && buyerAnonUser.get().getProduct() != null) {
            return buyerAnonUser.get().getProduct();
        } else if (sellerAnonUser.isPresent() && sellerAnonUser.get().getProduct() != null) {
            return sellerAnonUser.get().getProduct();
        } else {
            return null;
        }
    }

    public void markMessageAsRead(UUID messageId) {
        Optional<ChatMessage> messageOpt = chatMessageRepository.findById(messageId);
        if (messageOpt.isPresent()) {
            ChatMessage message = messageOpt.get();
            message.setRead(true);
            chatMessageRepository.save(message);
        } else {
            throw new IllegalStateException("Message not found with ID: " + messageId);
        }
    }

    private boolean isLatestBidderMatching(ChatSession session, Product product) {
        if (product != null && product.getProductStatus() == Product.ProductStatus.SOLD) {
            Bids latestBid = product.getLatestBid();
            if (latestBid != null) {
                AnonymousUser latestBidder = latestBid.getBidderAnonymous();
                return latestBidder != null && latestBidder.getAnonUserId().equals(session.getBuyerAnonId());
            }
        }
        return false;
    }

    private UUID getActualUserId(UUID anonId) {
        Optional<AnonymousUser> anonUserOpt = anonymousUserService.getAnonUserById(anonId);

        if (anonUserOpt.isPresent()) {
            User actualUser = anonUserOpt.get().getUser();

            return actualUser.getUserId();
        } else {
            throw new IllegalStateException("Anonymous user not found with ID: " + anonId);
        }
    }

    public int countUnreadMessages(String sessionId, UUID recipientUserId) {
        return chatMessageRepository.countBySessionIdAndRecipient_User_UserIdAndIsReadFalse(sessionId, recipientUserId);
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

    public UUID startChatSessionWithAnonIds(UUID buyerAnonId, UUID sellerAnonId) {
        ChatSession newSession = createChatSession(buyerAnonId, sellerAnonId);

        return newSession.getId();
    }
}
