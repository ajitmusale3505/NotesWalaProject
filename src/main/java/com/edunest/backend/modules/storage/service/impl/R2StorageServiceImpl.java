package com.edunest.backend.modules.storage.service.impl;

import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.modules.storage.dto.FileStreamResponse;
import com.edunest.backend.modules.storage.dto.UploadResponse;
import com.edunest.backend.modules.storage.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class R2StorageServiceImpl implements StorageService {

    private static final long MAX_UPLOAD_BYTES = 25L * 1024 * 1024;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf", "image/jpeg", "image/png", "image/webp");

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${cloudflare.r2.bucket}")
    private String bucketName;

    @Override
    public UploadResponse uploadPdf(MultipartFile file) {
        return uploadFile(file, "resources/main");
    }

    @Override
    public UploadResponse uploadFile(MultipartFile file, String folder) {
        validate(file);
        String safeFolder = folder == null ? "uploads" :
                folder.replaceAll("[^a-zA-Z0-9/_-]", "").replaceAll("/{2,}", "/");
        if (safeFolder.isBlank()) {
            throw new BadRequestException("Invalid upload folder");
        }

        String extension = extensionFor(file.getOriginalFilename());
        String key = safeFolder + "/" + UUID.randomUUID() + extension;

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(normalizeContentType(file.getContentType()))
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return UploadResponse.builder()
                    .fileName(key)
                    .fileUrl("r2://" + bucketName + "/" + key)
                    .build();
        } catch (IOException | S3Exception e) {
            throw new IllegalStateException("File upload failed", e);
        }
    }

    @Override
    public String generateImageUrl(String key) {
        return generatePresignedUrl(key, Duration.ofMinutes(15));
    }

    @Override
    public String generatePublicUrl(String key) {
        return key == null || key.isBlank() ? null : generatePresignedUrl(key, Duration.ofMinutes(15));
    }

    private String generatePresignedUrl(String key, Duration duration) {
        validateKey(key);
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName).key(key).build();

        GetObjectPresignRequest presign = GetObjectPresignRequest.builder()
                .signatureDuration(duration)
                .getObjectRequest(request)
                .build();

        return s3Presigner.presignGetObject(presign).url().toString();
    }

    @Override
    public FileStreamResponse getFile(String key) {
        validateKey(key);

        HeadObjectResponse metadata = s3Client.headObject(HeadObjectRequest.builder()
                .bucket(bucketName).key(key).build());

        ResponseInputStream<GetObjectResponse> stream = s3Client.getObject(
                GetObjectRequest.builder().bucket(bucketName).key(key).build());

        return FileStreamResponse.builder()
                .inputStream(stream)
                .contentType(metadata.contentType())
                .contentLength(metadata.contentLength())
                .fileName(key.substring(key.lastIndexOf('/') + 1))
                .build();
    }

    @Override
    public void deleteFile(String key) {
        validateKey(key);
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName).key(key).build());
        } catch (S3Exception ex) {
            throw new IllegalStateException("File cleanup failed", ex);
        }
    }

    private static void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new BadRequestException("File is required");
        if (file.getSize() > MAX_UPLOAD_BYTES) throw new BadRequestException("File exceeds 25 MB limit");

        String contentType = normalizeContentType(file.getContentType());
        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new BadRequestException("Unsupported file type");
        }
        if ("application/pdf".equals(contentType)
                && !extensionFor(file.getOriginalFilename()).equals(".pdf")) {
            throw new BadRequestException("PDF uploads must have a .pdf extension");
        }
    }

    private static String normalizeContentType(String contentType) {
        return contentType == null ? "" : contentType.toLowerCase(Locale.ROOT);
    }

    private static String extensionFor(String originalName) {
        if (originalName == null) return "";
        String name = originalName.toLowerCase(Locale.ROOT);
        int dot = name.lastIndexOf('.');
        return dot >= 0 ? name.substring(dot) : "";
    }

    private static void validateKey(String key) {
        if (key == null || key.isBlank() || key.contains("..")
                || key.startsWith("/") || key.contains("\\")) {
            throw new BadRequestException("Invalid storage key");
        }
    }
}
