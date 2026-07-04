package com.meraki.website.controller;

import com.meraki.website.service.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final MinioService minioService;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = minioService.uploadImage(file);
            return ResponseEntity.ok(fileName);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Upload failed: " + e.getMessage());
        }
    }
}