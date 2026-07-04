package com.meraki.website.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthLoginResponseDTO {
    private String token;
    private String userId;
    private String role;
    private Boolean mustChangePassword;
}

