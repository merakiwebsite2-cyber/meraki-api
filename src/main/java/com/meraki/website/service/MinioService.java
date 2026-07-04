package com.meraki.website.service;

import com.meraki.website.config.MinioProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final S3Client s3Client;
    private final MinioProperties props;

    public String uploadImage(MultipartFile file) throws IOException {

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(props.getBucket())
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromBytes(file.getBytes())
        );

        return generatePublicUrl(fileName);
    }
    private String generatePublicUrl(String fileName) {
        return String.format("%s%s/%s", props.getUrl(), props.getBucket(), fileName);
    }
}