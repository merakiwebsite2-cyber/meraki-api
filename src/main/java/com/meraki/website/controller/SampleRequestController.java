package com.meraki.website.controller;

import com.meraki.website.dto.ApiResponse;
import com.meraki.website.dto.SampleRequestCreateDto;
import com.meraki.website.dto.SampleRequestResponseDto;
import com.meraki.website.service.SampleRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sample-requests")
public class SampleRequestController {

    private final SampleRequestService sampleRequestService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<SampleRequestResponseDto>> create(@RequestBody SampleRequestCreateDto body) {
        ApiResponse<SampleRequestResponseDto> response = sampleRequestService.create(body);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<SampleRequestResponseDto>>> my(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        ApiResponse<Page<SampleRequestResponseDto>> response = sampleRequestService.listMy(status, page, size);
        return ResponseEntity.ok(response);
    }
}

