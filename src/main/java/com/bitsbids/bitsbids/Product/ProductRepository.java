package com.bitsbids.bitsbids.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.bitsbids.bitsbids.AnonymousUser.AnonymousUser;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findByAnonymousSeller(AnonymousUser anonymousSeller);

    List<Product> findByProductNameIgnoreCase(String productName);

    List<Product> findByProductStatus(Product.ProductStatus status);

    List<Product> findByProductIdIn(List<UUID> productIds);

    List<Product> findByUser_UserId(UUID userId);

}
