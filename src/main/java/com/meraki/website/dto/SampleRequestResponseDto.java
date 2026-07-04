package com.meraki.website.dto;

import com.meraki.website.entity.SampleRequest;
import com.meraki.website.entity.SampleRequestStatus;
import com.meraki.website.entity.Specification;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class SampleRequestResponseDto {
    private String id;

    private String userId;
    private String userEmail;
    private String userName;
    private String userMobileNo;
    private String userCompanyName;

    private String productId;
    private String variantId;

    private SampleRequestStatus status;

    private String message;

    private Specification requestedSpecification;
    private String requestedCategory;
    private String requestedCollection;

    private String requestedArticle;
    private String requestedColor;
    private String requestedMainImageUrl;
    private List<String> requestedImages;

    private String adminNotes;
    private Instant resolvedAt;
    private Instant createdAt;
    private Instant updatedAt;

    public static SampleRequestResponseDto from(SampleRequest sr) {
        return SampleRequestResponseDto.builder()
                .id(sr.getId())
                .userId(sr.getUserId())
                .userEmail(sr.getUserEmail())
                .userName(sr.getUserName())
                .userMobileNo(sr.getUserMobileNo())
                .userCompanyName(sr.getUserCompanyName())
                .productId(sr.getProductId())
                .variantId(sr.getVariantId())
                .status(sr.getStatus())
                .message(sr.getMessage())
                .requestedSpecification(sr.getRequestedSpecification())
                .requestedCategory(sr.getRequestedCategory())
                .requestedCollection(sr.getRequestedCollection())
                .requestedArticle(sr.getRequestedArticle())
                .requestedColor(sr.getRequestedColor())
                .requestedMainImageUrl(sr.getRequestedMainImageUrl())
                .requestedImages(sr.getRequestedImages())
                .adminNotes(sr.getAdminNotes())
                .resolvedAt(sr.getResolvedAt())
                .createdAt(sr.getCreatedAt())
                .updatedAt(sr.getUpdatedAt())
                .build();
    }
}

