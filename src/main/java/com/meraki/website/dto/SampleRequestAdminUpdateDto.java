package com.meraki.website.dto;

import com.meraki.website.entity.SampleRequestStatus;
import lombok.Data;

@Data
public class SampleRequestAdminUpdateDto {
    private SampleRequestStatus status;
    private String adminNotes;
}

