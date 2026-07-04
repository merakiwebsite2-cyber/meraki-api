package com.meraki.website.dto;

import com.meraki.website.entity.Specification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductWithUrlsDto {
    private String id;
    private Specification specification;
    private String category;
    private String subCategory;
    private String collection;
    private VariantWithUrlsDto defaultVariant;
    private List<String> certificates;
}

