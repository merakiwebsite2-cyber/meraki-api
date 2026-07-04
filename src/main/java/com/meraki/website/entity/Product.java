package com.meraki.website.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@Document(collection = "products")
@AllArgsConstructor
@NoArgsConstructor
public class Product
{
    @Id
    private String id;
    private Specification specification;
    private String category;//curtain,rugs
    private String subCategory;// indoor or outdoor
    private String collection;//Carent,greenlon
    private Variant defaultVariant;
    private List<String> certificates;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
