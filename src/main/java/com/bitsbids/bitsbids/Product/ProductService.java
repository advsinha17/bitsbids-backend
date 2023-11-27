package com.bitsbids.bitsbids.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bitsbids.bitsbids.AnonymousUser.AnonymousUser;
import com.bitsbids.bitsbids.AnonymousUser.AnonymousUserService;
import com.bitsbids.bitsbids.ElasticSearch.ProductIndex;
import com.bitsbids.bitsbids.ElasticSearch.ProductSearchRepository;
import com.bitsbids.bitsbids.Users.User;

import jakarta.transaction.Transactional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductSearchRepository productSearchRepository;

    @Autowired
    private AnonymousUserService anonymousUserService;

    public List<Product> getActiveProducts() {
        return productRepository.findByProductStatus(Product.ProductStatus.ACTIVE);
    }

    public Optional<Product> getProductById(UUID id) {
        return productRepository.findById(id);
    }

    public Product addNewProduct(Product product) {
        User optionalUser = product.getUser();
        UUID userId = optionalUser.getUserId();

        AnonymousUser anonymousUser = createAnonymousUser(userId);
        product.setAnonymousSeller(anonymousUser);
        product.setNumberOfBids(0);
        productRepository.save(product);

        ProductIndex productIndex = convertToProductIndex(product);
        productSearchRepository.save(productIndex);
        return product;
    }

    private AnonymousUser createAnonymousUser(UUID userId) {
        return anonymousUserService.addAnonymousUser(userId);
    }

    public List<Product> getProductsByIds(List<UUID> productIds) {
        return productRepository.findByProductIdIn(productIds);
    }

    public boolean updateProduct(UUID existingProductId, Product newProduct) {
        try {
            Optional<Product> optionalExistingProduct = productRepository.findById(existingProductId);

            if (optionalExistingProduct.isPresent()) {
                Product existingProduct = optionalExistingProduct.get();
                existingProduct.setMediaUrls(newProduct.getMediaUrls());
                existingProduct.setProductDescription(newProduct.getProductDescription());
                productRepository.save(existingProduct);
                return true;
            } else {
                return false;
            }
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

    private ProductIndex convertToProductIndex(Product product) {
        ProductIndex productIndex = new ProductIndex();
        productIndex.setId(product.getProductId().toString());
        productIndex.setName(product.getProductName());
        productIndex.setDescription(product.getProductDescription());
        return productIndex;
    }

}
