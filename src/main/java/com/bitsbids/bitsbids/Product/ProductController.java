package com.bitsbids.bitsbids.Product;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
@CrossOrigin
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable UUID productId) {
        return productService.getProductById(productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/new-product")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.addNewProduct(product);
        return ResponseEntity.ok(createdProduct);
    }

    @PostMapping("/{productId}/edit")
    public ResponseEntity<?> updateWalletBalance(@PathVariable UUID productId,
            @RequestBody Product newProduct) {
        boolean updateSuccess = productService.updateProduct(productId, newProduct);
        if (updateSuccess) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("Unable to update wallet balance.");
        }
    }

}
