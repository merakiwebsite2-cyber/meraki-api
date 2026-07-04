package com.meraki.website.service;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.meraki.website.dto.LoginRequestDTO;

@Service
public class LoginService {

    private final MongoTemplate mongoTemplate;

    public LoginService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public String login(LoginRequestDTO loginRequestDto) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(loginRequestDto.getUsername()));
        query.addCriteria(Criteria.where("password").is(loginRequestDto.getPassword()));
        LoginRequestDTO user = mongoTemplate.findOne(query, LoginRequestDTO.class,"admin");
        System.out.println(user);
        return "done";
    }

}
