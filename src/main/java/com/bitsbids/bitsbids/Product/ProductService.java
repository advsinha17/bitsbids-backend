package com.bitsbids.bitsbids.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<Product> getActiveProducts() {
        return productRepository.findByProductStatus(Product.ProductStatus.ACTIVE);
    }

    public Optional<Product> getProductById(UUID id) {
        return productRepository.findById(id);
    }

    public Product addNewProduct(Product product) {
        productRepository.save(product);
        return product;
    }
    // some logic to be added when form/listeners added

    public boolean updateProduct(Product existingProduct, Product newProduct) {
        try {
            existingProduct.setCategories(newProduct.getCategories());
            existingProduct.setMediaUrls(newProduct.getMediaUrls());
            existingProduct.setProductDescription(newProduct.getProductDescription());
            productRepository.save(existingProduct);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Optional<Product> updateProductStatus(UUID productId, Product.ProductStatus newStatus,
            Product.ProductStatus initialState) {
        Optional<Product> productOpt = productRepository.findById(productId);

        if (productOpt.isPresent()) {
            Product product = productOpt.get();

            if (product.getProductStatus().equals(initialState)) {
                product.setProductStatus(newStatus);
                productRepository.save(product);
                return Optional.of(product);
            }
        }

        return Optional.empty();
    }

    @Transactional
    public boolean deleteProduct(UUID id) {
        boolean exists = productRepository.existsById(id);
        if (!exists) {
            return false;
        }

        try {
            productRepository.deleteById(id);
            return !productRepository.existsById(id);
        } catch (Exception e) {
            return false;
        }
    }
}
