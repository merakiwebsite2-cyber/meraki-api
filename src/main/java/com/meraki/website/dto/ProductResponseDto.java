package com.meraki.website.dto;

import com.meraki.website.entity.Product;
import com.meraki.website.entity.Variant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {
    private Product product;
    private List<Variant> variants;


    public static ProductResponseDto of(Product product, List<Variant> variants) {
        return new ProductResponseDto(product, variants);
    }
}

