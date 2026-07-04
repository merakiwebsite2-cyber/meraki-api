package com.meraki.website.repository;

import com.meraki.website.entity.Variant;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface VariantRepository extends MongoRepository<Variant, String> {
    List<Variant> findByProductId(String productId);

    void deleteByProductId(String productId);
}

