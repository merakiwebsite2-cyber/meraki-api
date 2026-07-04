package com.meraki.website.controller;

import com.meraki.website.dto.ApiResponse;
import com.meraki.website.dto.SampleRequestAdminUpdateDto;
import com.meraki.website.dto.SampleRequestResponseDto;
import com.meraki.website.service.AdminSampleRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/sample-requests")
public class AdminSampleRequestController {

    private final AdminSampleRequestService adminSampleRequestService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<SampleRequestResponseDto>>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String userEmail,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        ApiResponse<Page<SampleRequestResponseDto>> response =
                adminSampleRequestService.list(status, userEmail, userId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SampleRequestResponseDto>> getById(@PathVariable String id) {
        ApiResponse<SampleRequestResponseDto> response = adminSampleRequestService.getById(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE, value = "/{id}")
    public ResponseEntity<ApiResponse<SampleRequestResponseDto>> update(
            @PathVariable String id,
            @RequestBody SampleRequestAdminUpdateDto body
    ) {
        ApiResponse<SampleRequestResponseDto> response = adminSampleRequestService.update(id, body);
        return ResponseEntity.ok(response);
    }
}

