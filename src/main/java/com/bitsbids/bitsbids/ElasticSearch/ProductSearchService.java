// package com.bitsbids.bitsbids.ElasticSearch;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
// import org.springframework.data.elasticsearch.core.SearchHits;
// import
// org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
// import org.elasticsearch.index.query.QueryBuilders;
// import org.elasticsearch.index.query.BoolQueryBuilder;
// import java.util.List;
// import java.util.stream.Collectors;

// public class ProductSearchService {

// @Autowired
// private ElasticsearchOperations elasticsearchOperations;

// public List<ProductIndex> searchByContextualQuery(String searchTerm) {
// BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
// .should(QueryBuilders.matchQuery("name", searchTerm))
// .should(QueryBuilders.matchQuery("description", searchTerm));

// var searchQuery = new NativeSearchQueryBuilder()
// .withQuery(queryBuilder)
// .build();

// SearchHits<ProductIndex> searchHits =
// elasticsearchOperations.search(searchQuery, ProductIndex.class);
// return searchHits.getSearchHits().stream()
// .map(hit -> hit.getContent())
// .collect(Collectors.toList());
// }
// }
