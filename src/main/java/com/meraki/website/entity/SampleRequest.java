package com.meraki.website.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "sample_requests")
public class SampleRequest {
    @Id
    private String id;

    // requester snapshot
    private String userId;
    private String userEmail;
    private String userName;
    private String userMobileNo;
    private String userCompanyName;

    // requested item identity
    private String productId;
    private String variantId; // nullable (null means product/default variant)

    private SampleRequestStatus status;

    // optional requester message / context
    private String message;

    // product snapshot
    private Specification requestedSpecification;
    private String requestedCategory;
    private String requestedCollection;

    // variant snapshot (may come from Variant or Product.defaultVariant)
    private String requestedArticle;
    private String requestedColor;
    private String requestedMainImageUrl;
    private List<String> requestedImages;

    // admin fields
    private String adminNotes;
    private Instant resolvedAt;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}

