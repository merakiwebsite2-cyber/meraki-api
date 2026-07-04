package com.meraki.website.dto;

import com.meraki.website.entity.User;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDTO {

    private String id;
    private String name;
    private String email;
    private String mobileNo;
    private String companyName;
    private String countryCode;
    private String country;
    private Boolean isVerified;
    private String role;
    private Boolean mustChangePassword;

    public static UserResponseDTO mapToResponse(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .mobileNo(user.getMobileNo())
                .companyName(user.getCompanyName())
                .countryCode(user.getCountryCode())
                .country(user.getCountry())
                .isVerified(user.getIsVerified())
                .role(user.getRole())
                .mustChangePassword(user.getMustChangePassword())
                .build();
    }
}