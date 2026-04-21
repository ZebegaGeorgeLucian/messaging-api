package com.messaging.demo.controller;

import com.messaging.demo.aws.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
@Slf4j
public class MediaController {

    private final S3Service s3Service;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadMedia(
            @RequestParam("file") MultipartFile file) throws IOException {

        log.info("Received media upload: {} ({} bytes)",
                file.getOriginalFilename(), file.getSize());

        String key = s3Service.uploadFile(
                file.getBytes(),
                file.getContentType());

        String presignedUrl = s3Service.generatePresignedUrl(key);

        return ResponseEntity.ok(Map.of(
                "key", key,
                "url", presignedUrl,
                "expiresIn", "1 hour"
        ));
    }

    @GetMapping("/url/{key}")
    public ResponseEntity<Map<String, String>> getPresignedUrl(
            @PathVariable String key) {

        String url = s3Service.generatePresignedUrl("mms/" + key);
        return ResponseEntity.ok(Map.of("url", url));
    }
}