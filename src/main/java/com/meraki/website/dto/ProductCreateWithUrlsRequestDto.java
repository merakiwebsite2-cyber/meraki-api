package com.meraki.website.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateWithUrlsRequestDto {
    private ProductWithUrlsDto product;
    private List<VariantWithUrlsDto> variantList;
}

