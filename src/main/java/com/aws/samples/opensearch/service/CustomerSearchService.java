package com.aws.samples.opensearch.service;

/*
 *        Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *        Permission is hereby granted, free of charge, to any person obtaining a copy of this
 *        software and associated documentation files (the "Software"), to deal in the Software
 *        without restriction, including without limitation the rights to use, copy, modify,
 *        merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 *        permit persons to whom the Software is furnished to do so.
 *
 *        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *        INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 *        PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *        HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 *        OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *        SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import com.aws.samples.opensearch.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.QueryBuilders;
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder;
import org.opensearch.data.client.osc.NativeQuery;
import org.opensearch.data.client.osc.NativeQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This service uses ElasticSearch High level client libraries to query OpenSearch
 *
 * @author Angel Conde
 */
@Service
@Slf4j
public class CustomerSearchService {

    private static final String BOOTFULSEARCH = "bootfulsearch";

    private final ElasticsearchOperations elasticsearchOperations;

    private  OpenSearchClient client;

    @Autowired
    public CustomerSearchService(final ElasticsearchOperations elasticsearchOperations, OpenSearchClient client) {
        super();
        this.elasticsearchOperations = elasticsearchOperations;
        this.client = client;
    }

    public List<IndexedObjectInformation> createProductIndexBulk(final List<Customer> products) {

        List<IndexQuery> queries = products.stream()
                .map(product -> new IndexQueryBuilder().withId(product.getId()).withObject(product).build())
                .collect(Collectors.toList());

        return elasticsearchOperations.bulkIndex(queries, IndexCoordinates.of(BOOTFULSEARCH));

    }

    public String createProductIndex(Customer product) {

        IndexQuery indexQuery = new IndexQueryBuilder().withId(product.getId()).withObject(product).build();

        return elasticsearchOperations.index(indexQuery, IndexCoordinates.of(BOOTFULSEARCH));
    }

    public void findByLastName(final String lastName) {

        var match= QueryBuilders.match().field("lastname").query(FieldValue.of(lastName)).fuzziness("AUTO").build();

        NativeQuery searchQuery = new NativeQueryBuilder().withQuery(match).build();
        SearchHits<Customer> productHits =
                elasticsearchOperations
                        .search(searchQuery, Customer.class,
                                IndexCoordinates.of(BOOTFULSEARCH));

        log.info("productHits {} {}", productHits.getSearchHits().size(), productHits.getSearchHits());

        List<SearchHit<Customer>> searchHits =
                productHits.getSearchHits();
        int i = 0;
        for (SearchHit<Customer> searchHit : searchHits) {
            log.info("searchHit {}", searchHit);
        }

    }

    public void findByProductName(final String customerName) {
        Query searchQuery = new StringQuery(
                "{\"match\":{\"name\":{\"query\":\"" + customerName + "\"}}}\"");

        SearchHits<Customer> products = elasticsearchOperations.search(searchQuery, Customer.class,
                IndexCoordinates.of(BOOTFULSEARCH));
    }

    public void findByCustomerAge(final String productPrice) {
        Criteria criteria = new Criteria("price").greaterThan(10.0).lessThan(100.0);
        Query searchQuery = new CriteriaQuery(criteria);

        SearchHits<Customer> products = elasticsearchOperations.search(searchQuery, Customer.class,
                IndexCoordinates.of(BOOTFULSEARCH));
    }

    public List<Customer> processSearch(final String query) {
        log.info("Search with query {}", query);

        // 1. Create query on multiple fields enabling fuzzy search

        var match= QueryBuilders.multiMatch().fields("firstName","lastName").query(query).fuzziness("AUTO").build();

        NativeQuery searchQuery = new NativeQueryBuilder().withQuery(match).build();


        // 2. Execute search
        SearchHits<Customer> productHits =
                elasticsearchOperations
                        .search(searchQuery, Customer.class,
                                IndexCoordinates.of(BOOTFULSEARCH));

        // 3. Map searchHits to product list
        List<Customer> productMatches = new ArrayList<>();
        productHits.forEach(srchHit -> productMatches.add(srchHit.getContent()));
        return productMatches;
    }


    public List<String> fetchSuggestions(String query) {

        var match= QueryBuilders.wildcard().field("firstName").value(query + "*").build();
        NativeQuery searchQuery = new NativeQueryBuilder().withQuery(match).withPageable(PageRequest.of(0, 5)).build();

        SearchHits<Customer> searchSuggestions =
                elasticsearchOperations.search(searchQuery,
                        Customer.class,
                        IndexCoordinates.of(BOOTFULSEARCH));

        List<String> suggestions = new ArrayList<>();
        searchSuggestions.getSearchHits().forEach(searchHit -> suggestions.add(searchHit.getContent().getFirstName()));
        return suggestions;
    }
}