package com.meraki.website.repository;

import com.meraki.website.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}

