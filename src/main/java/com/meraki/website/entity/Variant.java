package com.meraki.website.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@Document(collection = "variants")
@AllArgsConstructor
@NoArgsConstructor
public class Variant
{
    @Id
    private String id;
    private String article;
    private String color;
    private String mainImageUrl;
    private List<String> images;
    private String productId;
}
