package com.bitsbids.bitsbids.ElasticSearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.annotations.Query;

import java.util.List;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductIndex, String> {

    List<ProductIndex> findByName(String name);

    List<ProductIndex> findByDescriptionContaining(String description);

    @Query("{\"bool\": {\"must\": [{\"match\": {\"name\": {\"query\": \"?0\", \"fuzziness\": \"AUTO\"}}} ]}}")
    List<ProductIndex> searchByName(String name);

    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name\", \"description\"], \"fuzziness\": \"AUTO\"}}")
    List<ProductIndex> searchByQuery(String query);
}