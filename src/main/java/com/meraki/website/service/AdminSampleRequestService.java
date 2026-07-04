package com.meraki.website.service;

import com.meraki.website.dto.ApiResponse;
import com.meraki.website.dto.SampleRequestAdminUpdateDto;
import com.meraki.website.dto.SampleRequestResponseDto;
import com.meraki.website.entity.SampleRequest;
import com.meraki.website.entity.SampleRequestStatus;
import com.meraki.website.repository.SampleRequestRepository;
import lombok.RequiredArgsConstructor;
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

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminSampleRequestService {

    private final SampleRequestRepository sampleRequestRepository;
    private final MongoTemplate mongoTemplate;

    public ApiResponse<Page<SampleRequestResponseDto>> list(
            String status,
            String userEmail,
            String userId,
            int page,
            int size
    ) {
        try {
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
            if (parsedStatus != null) {
                q.addCriteria(Criteria.where("status").is(parsedStatus));
            }
            if (StringUtils.hasText(userEmail)) {
                q.addCriteria(Criteria.where("userEmail").is(userEmail));
            }
            if (StringUtils.hasText(userId)) {
                q.addCriteria(Criteria.where("userId").is(userId));
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

    public ApiResponse<SampleRequestResponseDto> getById(String id) {
        try {
            if (!StringUtils.hasText(id)) {
                return new ApiResponse<>(false, "Missing id", null);
            }
            SampleRequest sr = sampleRequestRepository.findById(id).orElse(null);
            if (sr == null) {
                return new ApiResponse<>(false, "Sample request not found", null);
            }
            return new ApiResponse<>(true, "Sample request fetched", SampleRequestResponseDto.from(sr));
        } catch (Exception e) {
            return new ApiResponse<>(false, e.getMessage(), null);
        }
    }

    public ApiResponse<SampleRequestResponseDto> update(String id, SampleRequestAdminUpdateDto body) {
        try {
            if (!StringUtils.hasText(id)) {
                return new ApiResponse<>(false, "Missing id", null);
            }
            if (body == null) {
                return new ApiResponse<>(false, "Missing payload", null);
            }
            if (body.getStatus() == null) {
                return new ApiResponse<>(false, "Missing status", null);
            }

            SampleRequest sr = sampleRequestRepository.findById(id).orElse(null);
            if (sr == null) {
                return new ApiResponse<>(false, "Sample request not found", null);
            }

            if (sr.getStatus() != SampleRequestStatus.PENDING) {
                return new ApiResponse<>(false, "Only PENDING requests can be updated", null);
            }
            if (body.getStatus() == SampleRequestStatus.PENDING) {
                return new ApiResponse<>(false, "Invalid status transition", null);
            }

            sr.setStatus(body.getStatus());
            sr.setAdminNotes(StringUtils.hasText(body.getAdminNotes()) ? body.getAdminNotes().trim() : null);
            sr.setResolvedAt(Instant.now());

            SampleRequest saved = sampleRequestRepository.save(sr);
            return new ApiResponse<>(true, "Sample request updated", SampleRequestResponseDto.from(saved));
        } catch (Exception e) {
            return new ApiResponse<>(false, e.getMessage(), null);
        }
    }
}

