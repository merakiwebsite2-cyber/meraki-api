package com.meraki.website.dto;

import lombok.Data;

@Data
public class SampleRequestCreateDto {
    private String productId;
    private String variantId; // optional
    private String message;   // optional
}

