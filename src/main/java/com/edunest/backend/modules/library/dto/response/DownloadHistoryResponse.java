package com.edunest.backend.modules.library.dto.response;

import com.edunest.backend.common.enums.MaterialType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DownloadHistoryResponse {

    private Long resourceId;
    private String title;
    private MaterialType materialType;
    private String materialDisplayName;
    private LocalDateTime downloadedAt;
}
