package com.edunest.backend.modules.storage.service;

import java.io.InputStream;


import org.springframework.web.multipart.MultipartFile;

import com.edunest.backend.modules.storage.dto.UploadResponse;
import com.edunest.backend.modules.storage.dto.FileStreamResponse;

public interface StorageService {

    UploadResponse uploadPdf(MultipartFile file);

    UploadResponse uploadFile(MultipartFile file, String folder);

    // Keep this because thumbnails are working correctly
    String generateImageUrl(String key);

    // Keep this temporarily for thumbnail URLs
    String generatePublicUrl(String key);

    FileStreamResponse getFile(String key);

    void deleteFile(String key);
}