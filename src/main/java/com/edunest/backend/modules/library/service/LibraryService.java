package com.edunest.backend.modules.library.service;

import com.edunest.backend.common.enums.MaterialType;
import com.edunest.backend.modules.library.dto.response.DownloadHistoryResponse;
import com.edunest.backend.modules.library.dto.response.LibraryPurchaseResponse;
import com.edunest.backend.modules.library.dto.response.LibraryResourceResponse;
import com.edunest.backend.modules.library.dto.response.LibrarySubscriptionResponse;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface LibraryService {
    Page<LibraryResourceResponse> getPurchasedResources(Long userId, String keyword, MaterialType materialType, int page, int size);
    Page<LibraryResourceResponse> getSubscriptionResources(Long userId, String keyword, MaterialType materialType, int page, int size);
    Page<LibraryPurchaseResponse> getPurchaseHistory(Long userId, int page, int size);
    Optional<LibrarySubscriptionResponse> getActiveSubscription(Long userId);
    Page<DownloadHistoryResponse> getDownloadHistory(Long userId, int page, int size);
    String redownload(Long userId, Long resourceId);
}
