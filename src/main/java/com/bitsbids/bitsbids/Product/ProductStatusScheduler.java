package com.bitsbids.bitsbids.Product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductStatusScheduler {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Scheduled(fixedRate = 60000)
    public void updateProductStatuses() {
        LocalDateTime now = LocalDateTime.now();
        List<Product> allProducts = productService.getActiveProducts();

        for (Product product : allProducts) {
            if (product.getBidClosingTime().isBefore(now)) {
                if (product.getLatestBid() == null) {
                    product.setProductStatus(Product.ProductStatus.UNSOLD);
                } else {
                    product.setProductStatus(Product.ProductStatus.SOLD);
                }
                productRepository.save(product);
            }
        }
    }
}
