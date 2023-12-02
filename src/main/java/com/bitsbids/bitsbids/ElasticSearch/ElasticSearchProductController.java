package com.bitsbids.bitsbids.ElasticSearch;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "https://bitsbids-frontend-alno.vercel.app")
@RestController
@RequestMapping("/api/products")
public class ElasticSearchProductController {

    @Autowired
    private ProductSearchRepository productSearchRepository;

    @GetMapping("/search")
    public ResponseEntity<List<ProductIndex>> searchProducts(@RequestParam String query) {
        List<ProductIndex> products = productSearchRepository.searchByQuery(query);
        return ResponseEntity.ok(products);
    }
}