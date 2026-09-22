package com.edunest.backend.modules.storage.dto;

import java.io.InputStream;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileStreamResponse {

    private InputStream inputStream;

    private String contentType;

    private String fileName;

    private long contentLength;
}