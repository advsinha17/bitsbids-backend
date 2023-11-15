package com.bitsbids.bitsbids.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.bitsbids.bitsbids.AnonymousUser.AnonymousUser;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findByCategoriesIn(Set<Category> categories);

    Optional<Product> findByAnonymousSeller(AnonymousUser anonymousSeller);

    List<Product> findByProductNameIgnoreCase(String productName);

    List<Product> findByProductStatus(Product.ProductStatus status);

}
