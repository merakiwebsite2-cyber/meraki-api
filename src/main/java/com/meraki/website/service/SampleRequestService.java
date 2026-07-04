package com.meraki.website.service;

import com.meraki.website.dto.ApiResponse;
import com.meraki.website.dto.SampleRequestCreateDto;
import com.meraki.website.dto.SampleRequestResponseDto;
import com.meraki.website.entity.Product;
import com.meraki.website.entity.SampleRequest;
import com.meraki.website.entity.SampleRequestStatus;
import com.meraki.website.entity.User;
import com.meraki.website.entity.Variant;
import com.meraki.website.repository.ProductRepository;
import com.meraki.website.repository.SampleRequestRepository;
import com.meraki.website.repository.UserRepository;
import com.meraki.website.repository.VariantRepository;
import com.meraki.website.security.RequestUser;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SampleRequestService {

    private final SampleRequestRepository sampleRequestRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final VariantRepository variantRepository;
    private final MongoTemplate mongoTemplate;

    public ApiResponse<SampleRequestResponseDto> create(SampleRequestCreateDto body) {
        try {
            String userId = RequestUser.userIdOrNull();
            if (!StringUtils.hasText(userId)) {
                return new ApiResponse<>(false, "Unauthorized", null);
            }
            if (body == null) {
                return new ApiResponse<>(false, "Missing payload", null);
            }
            if (!StringUtils.hasText(body.getProductId())) {
                return new ApiResponse<>(false, "Missing productId", null);
            }

            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return new ApiResponse<>(false, "User not found", null);
            }

            Product product = productRepository.findById(body.getProductId()).orElse(null);
            if (product == null) {
                return new ApiResponse<>(false, "Product not found", null);
            }

            String variantId = StringUtils.hasText(body.getVariantId()) ? body.getVariantId() : null;
            Variant variant = null;
            if (variantId != null) {
                variant = variantRepository.findById(variantId).orElse(null);
                if (variant == null) {
                    return new ApiResponse<>(false, "Variant not found", null);
                }
                if (!StringUtils.hasText(variant.getProductId()) || !product.getId().equals(variant.getProductId())) {
                    return new ApiResponse<>(false, "Variant does not belong to product", null);
                }
            }

            boolean alreadyPending = sampleRequestRepository.existsByUserIdAndProductIdAndVariantIdAndStatus(
                    userId,
                    product.getId(),
                    variantId,
                    SampleRequestStatus.PENDING
            );
            if (alreadyPending) {
                return new ApiResponse<>(false, "Sample request already pending for this product/variant", null);
            }

            Variant snapshotVariant = (variant != null) ? variant : product.getDefaultVariant();

            SampleRequest sr = SampleRequest.builder()
                    .userId(user.getId())
                    .userEmail(user.getEmail())
                    .userName(user.getName())
                    .userMobileNo(user.getMobileNo())
                    .userCompanyName(user.getCompanyName())
                    .productId(product.getId())
                    .variantId(variantId)
                    .status(SampleRequestStatus.PENDING)
                    .message(StringUtils.hasText(body.getMessage()) ? body.getMessage().trim() : null)
                    .requestedSpecification(product.getSpecification())
                    .requestedCategory(product.getCategory())
                    .requestedCollection(product.getCollection())
                    .requestedArticle(snapshotVariant != null ? snapshotVariant.getArticle() : null)
                    .requestedColor(snapshotVariant != null ? snapshotVariant.getColor() : null)
                    .requestedMainImageUrl(snapshotVariant != null ? snapshotVariant.getMainImageUrl() : null)
                    .requestedImages(snapshotVariant != null ? snapshotVariant.getImages() : null)
                    .build();

            SampleRequest saved = sampleRequestRepository.save(sr);
            return new ApiResponse<>(true, "Sample request created", SampleRequestResponseDto.from(saved));
        } catch (DuplicateKeyException e) {
            return new ApiResponse<>(false, "Sample request already pending for this product/variant", null);
        } catch (Exception e) {
            return new ApiResponse<>(false, e.getMessage(), null);
        }
    }

    public ApiResponse<Page<SampleRequestResponseDto>> listMy(String status, int page, int size) {
        try {
            String userId = RequestUser.userIdOrNull();
            if (!StringUtils.hasText(userId)) {
                return new ApiResponse<>(false, "Unauthorized", Page.empty());
            }

            SampleRequestStatus parsedStatus = null;
            if (StringUtils.hasText(status)) {
                try {
                    parsedStatus = SampleRequestStatus.valueOf(status.trim().toUpperCase());
                } catch (IllegalArgumentException ignored) {
                    return new ApiResponse<>(false, "Invalid status", Page.empty());
                }
            }

            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Query q = new Query();
            q.addCriteria(Criteria.where("userId").is(userId));
            if (parsedStatus != null) {
                q.addCriteria(Criteria.where("status").is(parsedStatus));
            }

            long total = mongoTemplate.count(q, SampleRequest.class);
            q.with(pageable);
            List<SampleRequest> rows = mongoTemplate.find(q, SampleRequest.class);
            List<SampleRequestResponseDto> dtos = rows.stream().map(SampleRequestResponseDto::from).toList();
            return new ApiResponse<>(true, "Sample requests fetched", new PageImpl<>(dtos, pageable, total));
        } catch (Exception e) {
            return new ApiResponse<>(false, e.getMessage(), Page.empty());
        }
    }
}

