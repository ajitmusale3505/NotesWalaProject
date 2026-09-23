package com.edunest.backend.modules.storage.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadResponse {

    private String fileName;
    private String fileUrl;
    private String originalFileName;
    private String contentType;
    private long fileSizeBytes;
}