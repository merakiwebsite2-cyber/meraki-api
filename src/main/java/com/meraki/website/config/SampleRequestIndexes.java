package com.meraki.website.config;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.meraki.website.entity.SampleRequest;
import com.meraki.website.entity.SampleRequestStatus;
import org.bson.Document;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import jakarta.annotation.PostConstruct;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SampleRequestIndexes {

    private final MongoTemplate mongoTemplate;

    @PostConstruct
    public void ensureIndexes() {
        try {
            String collectionName = mongoTemplate.getCollectionName(SampleRequest.class);
            MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);

            Document partial = new Document("status", SampleRequestStatus.PENDING.name());
            IndexOptions options = new IndexOptions()
                    .name("uniq_pending_by_user_product_variant")
                    .unique(true)
                    .partialFilterExpression(partial);

            collection.createIndex(
                    Indexes.compoundIndex(
                            Indexes.ascending("userId"),
                            Indexes.ascending("productId"),
                            Indexes.ascending("variantId")
                    ),
                    options
            );
        } catch (Exception e) {
            log.warn("Failed ensuring sample request indexes", e);
        }
    }
}

