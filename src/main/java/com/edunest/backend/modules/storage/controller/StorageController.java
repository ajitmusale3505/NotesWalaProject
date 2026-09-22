package com.edunest.backend.modules.storage.controller;

import org.springframework.http.ResponseEntity;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.storage.dto.UploadResponse;
import com.edunest.backend.modules.storage.service.StorageService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;

import java.net.URI;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;

    @PreAuthorize("hasAnyRole('ADMIN','CONTRIBUTOR')")
    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<UploadResponse>> upload(
            @RequestPart("file") MultipartFile file) {

        UploadResponse uploadResponse =
                storageService.uploadPdf(file);

        ApiResponse<UploadResponse> response =
                ApiResponse.<UploadResponse>builder()
                        .success(true)
                        .message("File uploaded successfully")
                        .data(uploadResponse)
                        .build();

        return ResponseEntity.ok(response);
    }
    
    
    @GetMapping("/image/**")
    public ResponseEntity<Void> image(HttpServletRequest request) {

        String marker = "/files/image/";
        String uri = request.getRequestURI();
        int index = uri.indexOf(marker);
        if (index < 0) {
            return ResponseEntity.badRequest().build();
        }
        String key = uri.substring(index + marker.length());

        String url = storageService.generateImageUrl(key);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url))
                .build();
    }
    
    
}