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

import jakarta.transaction.Transactional;

@Service
public class BidsService {

    @Autowired
    private BidsRepository bidsRepository;

    @Autowired
    private AnonymousUserService anonymousUserService;

    @Autowired
    private ProductService productService;

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
        User optionalUser = newBid.getUser();
        UUID userId = optionalUser.getUserId();

        AnonymousUser anonymousUser = createAnonymousUser(userId);
        newBid.setBidderAnonymous(anonymousUser);

        Optional<Product> optionalProduct = productService.getProductById(productId);
        if (!optionalProduct.isPresent()) {
            throw new RuntimeException("Product not found.");
        }

        Product product = optionalProduct.get();

        BigDecimal latestBidAmount = product.getLatestBidAmount();
        if (latestBidAmount != null && newBid.getBidAmount().compareTo(latestBidAmount) <= 0) {
            throw new RuntimeException("Bid amount must be higher than the current bid.");
        }

        product.setLatestBidAmount(newBid.getBidAmount());
        product.setLatestBid(newBid);
        productService.updateProduct(productId, product);

        newBid.setProduct(product);
        bidsRepository.save(newBid);
        return newBid;
    }

    private AnonymousUser createAnonymousUser(UUID userId) {
        return anonymousUserService.addAnonymousUser(userId);
    }
}
