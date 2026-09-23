package com.edunest.backend.modules.storage.service.impl;

import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.modules.storage.dto.FileStreamResponse;
import com.edunest.backend.modules.storage.dto.PdfUploadResponse;
import com.edunest.backend.modules.storage.dto.UploadResponse;
import com.edunest.backend.modules.storage.service.MalwareScanner;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class R2StorageServiceImpl implements StorageService {

    private static final long DEFAULT_MAX_UPLOAD_BYTES = 25L * 1024 * 1024;
    private static final long DEFAULT_MAX_IMAGE_BYTES = 5L * 1024 * 1024;
    private static final Duration DEFAULT_SIGNED_URL_LIFETIME = Duration.ofMinutes(15);
    private static final Duration MAX_SIGNED_URL_LIFETIME = Duration.ofHours(12);

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final MalwareScanner malwareScanner;
    private final PdfPreviewGenerator previewGenerator;

    @Value("${cloudflare.r2.bucket}")
    private String bucketName;

    @Value("${app.storage.max-pdf-size-bytes:26214400}")
    private long maxPdfBytes;

    @Value("${app.storage.max-image-size-bytes:5242880}")
    private long maxImageBytes;

    @Value("${app.storage.signed-url-lifetime-minutes:15}")
    private long signedUrlLifetimeMinutes;

    @Override
    public UploadResponse uploadPdf(MultipartFile file) {
        return uploadFile(file, "resources/main");
    }

    @Override
    public UploadResponse uploadFile(MultipartFile file, String folder) {
        validateGenericFile(file);
        return putObject(file, sanitizeFolder(folder));
    }

    @Override
    public UploadResponse uploadImage(MultipartFile file, String folder) {
        StorageFileValidator.validateImage(file, maxImageBytes);
        return putObject(file, sanitizeFolder(folder));
    }

    @Override
    public PdfUploadResponse uploadPdfWithPreview(
            MultipartFile file, String mainFolder, String previewFolder) {

        StorageFileValidator.validatePdf(file, maxPdfBytes);

        byte[] pdfBytes;
        try {
            pdfBytes = file.getBytes();
        } catch (IOException ex) {
            throw new BadRequestException("Unable to read uploaded PDF");
        }

        MalwareScanner.ScanResult scan = malwareScanner.scan(pdfBytes);
        if (!scan.clean()) {
            throw new BadRequestException("Uploaded file failed malware scanning");
        }

        PdfPreviewGenerator.GeneratedPreview generated = previewGenerator.generate(pdfBytes);
        String mainKey = null;
        String previewKey = null;

        try {
            mainKey = putBytes(
                    pdfBytes,
                    "application/pdf",
                    sanitizeFolder(mainFolder) + "/" + UUID.randomUUID() + ".pdf",
                    file.getOriginalFilename());

            previewKey = putBytes(
                    generated.getContent(),
                    "image/png",
                    sanitizeFolder(previewFolder) + "/" + UUID.randomUUID() + ".png",
                    "preview.png");

            return PdfUploadResponse.builder()
                    .mainFile(UploadResponse.builder()
                            .fileName(mainKey)
                            .fileUrl("r2://" + bucketName + "/" + mainKey)
                            .originalFileName(file.getOriginalFilename())
                            .contentType("application/pdf")
                            .fileSizeBytes(pdfBytes.length)
                            .build())
                    .previewFile(UploadResponse.builder()
                            .fileName(previewKey)
                            .fileUrl("r2://" + bucketName + "/" + previewKey)
                            .originalFileName("preview.png")
                            .contentType("image/png")
                            .fileSizeBytes(generated.getContent().length)
                            .build())
                    .pageCount(generated.getPageCount())
                    .previewPages(generated.getPreviewPages())
                    .build();
        } catch (RuntimeException ex) {
            deleteQuietly(mainKey);
            deleteQuietly(previewKey);
            throw ex;
        }
    }

    @Override
    public String generatePresignedUrl(String key, Duration duration) {
        validateKey(key);
        Duration safeDuration = duration == null ? configuredSignedUrlLifetime() : duration;
        if (safeDuration.isNegative() || safeDuration.isZero()
                || safeDuration.compareTo(MAX_SIGNED_URL_LIFETIME) > 0) {
            throw new BadRequestException("Invalid presigned URL duration");
        }
        return generatePresignedUrlInternal(key, safeDuration);
    }

    @Override
    public String generateImageUrl(String key) {
        return key == null || key.isBlank()
                ? null
                : generatePresignedUrlInternal(key, configuredSignedUrlLifetime());
    }

    @Override
    public String generatePublicUrl(String key) {
        return key == null || key.isBlank()
                ? null
                : generatePresignedUrlInternal(key, configuredSignedUrlLifetime());
    }

    private String generatePresignedUrlInternal(String key, Duration duration) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presign = GetObjectPresignRequest.builder()
                .signatureDuration(duration)
                .getObjectRequest(request)
                .build();

        return s3Presigner.presignGetObject(presign).url().toString();
    }

    @Override
    public FileStreamResponse getFile(String key) {
        validateKey(key);

        try {
            HeadObjectResponse metadata = s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());

            ResponseInputStream<GetObjectResponse> stream = s3Client.getObject(
                    GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build());

            return FileStreamResponse.builder()
                    .inputStream(stream)
                    .contentType(metadata.contentType())
                    .contentLength(metadata.contentLength())
                    .fileName(key.substring(key.lastIndexOf('/') + 1))
                    .build();
        } catch (NoSuchKeyException ex) {
            throw new BadRequestException("Stored file not found");
        } catch (S3Exception ex) {
            throw new IllegalStateException("Unable to retrieve stored file", ex);
        }
    }

    @Override
    public void deleteFile(String key) {
        validateKey(key);
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
        } catch (S3Exception ex) {
            throw new IllegalStateException("File deletion failed", ex);
        }
    }

    private UploadResponse putObject(MultipartFile file, String folder) {
        try {
            String key = folder + "/" + UUID.randomUUID() + extensionFor(file.getOriginalFilename());
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
                    .originalFileName(file.getOriginalFilename())
                    .contentType(normalizeContentType(file.getContentType()))
                    .fileSizeBytes(file.getSize())
                    .build();
        } catch (IOException | S3Exception ex) {
            throw new IllegalStateException("File upload failed", ex);
        }
    }

    private String putBytes(byte[] content, String contentType, String key, String originalName) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .contentLength((long) content.length)
                    .metadata(java.util.Map.of("original-filename",
                            originalName == null ? "file" : sanitizeMetadata(originalName)))
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(content));
            return key;
        } catch (S3Exception ex) {
            throw new IllegalStateException("File upload failed", ex);
        }
    }

    private void validateGenericFile(MultipartFile file) {
        StorageFileValidator.validateGeneric(file, maxPdfBytes);
        String type = normalizeContentType(file.getContentType());
        if (!"application/pdf".equals(type) && file.getSize() > maxImageBytes) {
            throw new BadRequestException("Image exceeds the maximum allowed size");
        }
        if ("application/pdf".equals(type)) {
            try {
                byte[] bytes = file.getBytes();
                MalwareScanner.ScanResult scan = malwareScanner.scan(bytes);
                if (!scan.clean()) {
                    throw new BadRequestException("Uploaded file failed malware scanning");
                }
            } catch (IOException ex) {
                throw new BadRequestException("Unable to read uploaded file");
            }
        }
    }

    private Duration configuredSignedUrlLifetime() {
        if (signedUrlLifetimeMinutes <= 0
                || signedUrlLifetimeMinutes > MAX_SIGNED_URL_LIFETIME.toMinutes()) {
            throw new IllegalStateException("Invalid configured signed URL lifetime");
        }
        return Duration.ofMinutes(signedUrlLifetimeMinutes);
    }

    private String sanitizeFolder(String folder) {
        String safe = folder == null ? "uploads" : folder.trim()
                .replaceAll("[^a-zA-Z0-9/_-]", "")
                .replaceAll("/{2,}", "/");
        if (safe.isBlank() || safe.startsWith("/") || safe.contains("..")) {
            throw new BadRequestException("Invalid upload folder");
        }
        return safe;
    }

    private static void validateKey(String key) {
        if (key == null || key.isBlank() || key.startsWith("/")
                || key.contains("..") || key.contains("\\")) {
            throw new BadRequestException("Invalid storage key");
        }
    }

    private static String normalizeContentType(String contentType) {
        return contentType == null ? "" : contentType.toLowerCase(Locale.ROOT).trim();
    }

    private static String extensionFor(String originalName) {
        if (originalName == null) return "";
        String name = originalName.toLowerCase(Locale.ROOT);
        int dot = name.lastIndexOf('.');
        return dot >= 0 ? name.substring(dot) : "";
    }

    private static String sanitizeMetadata(String value) {
        return value.replaceAll("[\\r\\n]", "_");
    }

    private void deleteQuietly(String key) {
        if (key == null || key.isBlank()) return;
        try {
            deleteFile(key);
        } catch (Exception ignored) {
        }
    }
}