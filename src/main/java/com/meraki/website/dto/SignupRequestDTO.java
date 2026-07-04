package com.meraki.website.dto;

import lombok.Data;

@Data
public class SignupRequestDTO {
    private String name;
    private String email;
    private String mobileNo;
    private String companyName;
    private String countryCode;
    private String country;

}

