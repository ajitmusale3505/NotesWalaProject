package com.edunest.backend.modules.storage.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PdfUploadResponse {
    private UploadResponse mainFile;
    private UploadResponse previewFile;
    private int pageCount;
    private int previewPages;
}