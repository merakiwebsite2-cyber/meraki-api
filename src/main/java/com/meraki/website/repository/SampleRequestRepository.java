package com.meraki.website.repository;

import com.meraki.website.entity.SampleRequest;
import com.meraki.website.entity.SampleRequestStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SampleRequestRepository extends MongoRepository<SampleRequest, String> {
    boolean existsByUserIdAndProductIdAndVariantIdAndStatus(
            String userId,
            String productId,
            String variantId,
            SampleRequestStatus status
    );
}

