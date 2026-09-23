package com.edunest.backend.modules.storage.service;

import java.time.Duration;

import org.springframework.web.multipart.MultipartFile;

import com.edunest.backend.modules.storage.dto.FileStreamResponse;
import com.edunest.backend.modules.storage.dto.PdfUploadResponse;
import com.edunest.backend.modules.storage.dto.UploadResponse;

public interface StorageService {

    UploadResponse uploadPdf(MultipartFile file);

    UploadResponse uploadFile(MultipartFile file, String folder);

    PdfUploadResponse uploadPdfWithPreview(MultipartFile file, String mainFolder, String previewFolder);

    String generateImageUrl(String key);

    String generatePublicUrl(String key);

    String generatePresignedUrl(String key, Duration duration);

    FileStreamResponse getFile(String key);

    void deleteFile(String key);
}