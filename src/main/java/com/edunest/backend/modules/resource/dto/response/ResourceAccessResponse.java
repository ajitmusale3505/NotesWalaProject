package com.edunest.backend.modules.resource.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceAccessResponse {

    // legacy flag
    private boolean allowed;

    private String message;

    // frontend access state
    private boolean canPreview;
    private boolean canViewFull;
    private boolean canDownload;
    private boolean requiresPurchase;

    // optional signed URLs
    private String viewUrl;
    private String downloadUrl;
}