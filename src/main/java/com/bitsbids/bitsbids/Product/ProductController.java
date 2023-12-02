package com.bitsbids.bitsbids.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bitsbids.bitsbids.Users.User;
import com.bitsbids.bitsbids.Users.UserService;

@RestController
@RequestMapping("/products")
@CrossOrigin
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Product>> getActiveProducts() {
        List<Product> products = productService.getActiveProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable UUID productId) {
        return productService.getProductById(productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Product>> getProductsByUserId(@PathVariable UUID userId) {
        List<Product> products = productService.getProductsByUserId(userId);
        if (products.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.addNewProduct(product);
        return ResponseEntity.ok(createdProduct);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> updateWalletBalance(@PathVariable UUID productId,
            @RequestBody Product newProduct) {
        boolean updateSuccess = productService.updateProduct(productId, newProduct);
        if (updateSuccess) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("Unable to update wallet balance.");
        }
    }

    @GetMapping("/user/bids/{userId}")
    public ResponseEntity<List<Product>> getProductsForUserBids(@PathVariable UUID userId) {
        List<Product> products = productService.findAllProductsForUserBids(userId);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{productId}/changeBidStatus")
    public ResponseEntity<?> freezeBid(@PathVariable UUID productId) {
        return productService.getProductById(productId)
                .map(product -> {
                    if (product.getLatestBid() != null && product.getLatestBid().getUser() != null) {
                        User winningUser = product.getLatestBid().getUser();
                        BigDecimal bidAmount = product.getLatestBid().getBidAmount();

                        // Check if the user has enough balance
                        if (winningUser.getWalletBalance().compareTo(bidAmount) >= 0) {
                            // Deduct bid amount from the user's wallet and bidAmount
                            userService.updateWalletBalance(winningUser.getUserId(), bidAmount.negate());
                            userService.decreaseBidAmount(winningUser.getUserId(), bidAmount);
                            product.setProductStatus(Product.ProductStatus.SOLD);
                        } else {
                            // Handle insufficient balance
                            return ResponseEntity.badRequest().body("User has insufficient balance.");
                        }
                    } else {
                        product.setProductStatus(Product.ProductStatus.WITHDRAWN);
                    }

                    productService.saveProduct(product);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Product>> getProductsByIds(@RequestBody List<UUID> productIds) {
        List<Product> products = productService.getProductsByIds(productIds);
        return ResponseEntity.ok(products);
    }

}
