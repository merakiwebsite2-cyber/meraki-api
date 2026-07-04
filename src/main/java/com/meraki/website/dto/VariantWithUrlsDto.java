package com.meraki.website.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariantWithUrlsDto {
    private String id;
    private String article;
    private String color;
    private String mainImageUrl;
    private List<String> images;
}

