package com.bitsbids.bitsbids.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bitsbids.bitsbids.AnonymousUser.AnonymousUser;
import com.bitsbids.bitsbids.AnonymousUser.AnonymousUserService;
import com.bitsbids.bitsbids.Bids.Bids;
import com.bitsbids.bitsbids.Bids.BidsRepository;
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

    private AnonymousUserService anonymousUserService;

    @Autowired
    private BidsRepository bidsRepository;

    @Autowired
    public void setAnonymousUserService(AnonymousUserService anonymousUserService) {
        this.anonymousUserService = anonymousUserService;
    }

    public List<Product> getActiveProducts() {
        return productRepository.findByProductStatus(Product.ProductStatus.ACTIVE);
    }

    public Optional<Product> getProductById(UUID id) {
        return productRepository.findById(id);
    }

    public List<Product> getProductsByUserId(UUID userId) {
        // Your repository should have a method to find products by user ID
        return productRepository.findByUser_UserId(userId);
    }

    public List<Product> findAllProductsByIds(Iterable<UUID> productIds) {
        return productRepository.findAllById(productIds);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public Product addNewProduct(Product product) {
        User user = product.getUser();
        UUID userId = user.getUserId();

        product.setNumberOfBids(0);
        product = productRepository.save(product);
        AnonymousUser anonymousUser = createAnonymousUser(userId, AnonymousUser.UserRole.SELLER,
                product.getProductId());
        product.setAnonymousSeller(anonymousUser);

        productRepository.save(product);

        ProductIndex productIndex = convertToProductIndex(product);
        productSearchRepository.save(productIndex);

        return product;
    }

    public List<Product> findAllProductsForUserBids(UUID userId) {
        List<Bids> userBids = bidsRepository.findByUser_UserId(userId);

        List<UUID> productIds = userBids.stream()
                .map(bid -> bid.getProduct().getProductId())
                .distinct()
                .collect(Collectors.toList());

        return findAllProductsByIds(productIds);
    }

    private AnonymousUser createAnonymousUser(UUID userId, AnonymousUser.UserRole role, UUID productId) {
        AnonymousUser anonUser = anonymousUserService.addAnonymousUser(userId, role);
        Optional<Product> optProduct = getProductById(productId);
        if (optProduct.isPresent()) {
            Product product = optProduct.get();
            anonUser.setProduct(product);
            return anonymousUserService.saveAnonymousUser(anonUser);
        }
        throw new IllegalStateException("Product not found");

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
