package com.meraki.website.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.meraki.website.entity.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByMobileNo(String mobileNo);
}