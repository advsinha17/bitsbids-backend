package com.bitsbids.bitsbids.Bids;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.bitsbids.bitsbids.AnonymousUser.AnonymousUser;
import com.bitsbids.bitsbids.Product.Product;
import com.bitsbids.bitsbids.Users.User;

@Repository
public interface BidsRepository extends JpaRepository<Bids, UUID> {
    List<Bids> findByBidderAnonymous(AnonymousUser bidderAnonymous);

    List<Bids> findByBidderAnonymous_AnonUserId(UUID anonId);

    List<Bids> findByProduct(Product product);

    List<Bids> findByProduct_ProductId(UUID productId);

    List<Bids> findByUser(User user);

    List<Bids> findByUser_UserId(UUID userId);

    // List<Bids> findByProduct_Id(UUID productId);
}