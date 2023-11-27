package com.bitsbids.bitsbids.Bids;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bitsbids.bitsbids.AnonymousUser.AnonymousUser;
import com.bitsbids.bitsbids.AnonymousUser.AnonymousUserService;
import com.bitsbids.bitsbids.Product.Product;
import com.bitsbids.bitsbids.Product.ProductService;
import com.bitsbids.bitsbids.Users.User;
import com.bitsbids.bitsbids.Users.UserService;
import com.bitsbids.bitsbids.Users.UserService.InsufficientBalanceException;

import jakarta.transaction.Transactional;

@Service
public class BidsService {

    @Autowired
    private BidsRepository bidsRepository;

    @Autowired
    private AnonymousUserService anonymousUserService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    public Optional<Bids> getBidById(UUID id) {
        return bidsRepository.findById(id);
    }

    public List<Bids> getBidsByProductID(UUID productId) {
        return bidsRepository.findByProduct_ProductId(productId);
    }

    public List<Bids> getBidsByProduct(Product product) {
        return bidsRepository.findByProduct(product);
    }

    public List<Bids> getBidsByUser(User user) {
        return bidsRepository.findByUser(user);
    }

    public List<Bids> getBidsByUserID(UUID userId) {
        return bidsRepository.findByUser_UserId(userId);
    }

    public List<Bids> getBidsByAnonUser(AnonymousUser anonUser) {
        return bidsRepository.findByBidderAnonymous(anonUser);
    }

    public List<Bids> getBidsByAnonUserID(UUID anonUserId) {
        return bidsRepository.findByBidderAnonymous_AnonUserId(anonUserId);
    }

    @Transactional
    public Bids addNewBid(UUID productId, Bids newBid) {
        User user = newBid.getUser();
        UUID userId = user.getUserId();

        Optional<Product> optionalProduct = productService.getProductById(productId);
        if (!optionalProduct.isPresent()) {
            throw new RuntimeException("Product not found.");
        }
        Product product = optionalProduct.get();

        Optional<AnonymousUser> existingAnonUser = anonymousUserService.findAnonUserForUserAndProduct(userId, productId,
                AnonymousUser.UserRole.BIDDER);
        AnonymousUser anonymousUser;
        if (existingAnonUser.isPresent()) {
            anonymousUser = existingAnonUser.get();
        } else {
            anonymousUser = createAnonymousUser(userId, AnonymousUser.UserRole.BIDDER, productId);
        }
        newBid.setBidderAnonymous(anonymousUser);

        BigDecimal latestBidAmount = product.getLatestBidAmount();
        if (latestBidAmount != null && newBid.getBidAmount().compareTo(latestBidAmount) <= 0) {
            throw new RuntimeException("Bid amount must be higher than the current bid.");
        }

        try {
            userService.updateBidAmount(userId, newBid.getBidAmount());
        } catch (InsufficientBalanceException e) {
            throw new IllegalStateException("Insufficient wallet balance...");
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("User not found.");
        }

        Bids oldBid = product.getLatestBid();
        if (oldBid != null) {
            User oldUser = oldBid.getUser();
            try {
                userService.decreaseBidAmount(oldUser.getUserId(), oldBid.getBidAmount());
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Old User not found.");
            }
        }
        product.setNumberOfBids(product.getNumberOfBids() + 1);
        product.setLatestBidAmount(newBid.getBidAmount());
        product.setLatestBid(newBid);
        productService.updateProduct(productId, product);

        newBid.setProduct(product);
        return bidsRepository.save(newBid);
    }

    public Optional<Bids> getLatestBidByUserOnProduct(UUID userId, UUID productId) {
        return bidsRepository.findTopByUserUserIdAndProductProductIdOrderByBidTimeDesc(userId, productId);
    }

    private AnonymousUser createAnonymousUser(UUID userId, AnonymousUser.UserRole role, UUID productId) {
        AnonymousUser anonUser = anonymousUserService.addAnonymousUser(userId, role);
        Optional<Product> optProduct = productService.getProductById(productId);
        if (optProduct.isPresent()) {
            Product product = optProduct.get();
            anonUser.setProduct(product);
            return anonymousUserService.saveAnonymousUser(anonUser);
        }
        throw new IllegalStateException("Product not found");

    }

}
