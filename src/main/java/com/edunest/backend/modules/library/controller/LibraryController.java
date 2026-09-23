package com.edunest.backend.modules.library.controller;

import com.edunest.backend.common.enums.MaterialType;
import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.library.dto.response.*;
import com.edunest.backend.modules.library.service.LibraryService;
import com.edunest.backend.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
public class LibraryController {

    private final LibraryService libraryService;

    @GetMapping("/purchased")
    public ResponseEntity<ApiResponse<Page<LibraryResourceResponse>>> purchased(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) MaterialType materialType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(ApiResponse.<Page<LibraryResourceResponse>>builder()
                .success(true)
                .message("Purchased resources fetched successfully")
                .data(libraryService.getPurchasedResources(
                        SecurityUtils.getCurrentUserId(), keyword, materialType, page, size))
                .build());
    }

    @GetMapping("/subscription/resources")
    public ResponseEntity<ApiResponse<Page<LibraryResourceResponse>>> subscriptionResources(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) MaterialType materialType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(ApiResponse.<Page<LibraryResourceResponse>>builder()
                .success(true)
                .message("Subscription resources fetched successfully")
                .data(libraryService.getSubscriptionResources(
                        SecurityUtils.getCurrentUserId(), keyword, materialType, page, size))
                .build());
    }

    @GetMapping("/purchases")
    public ResponseEntity<ApiResponse<Page<LibraryPurchaseResponse>>> purchaseHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(ApiResponse.<Page<LibraryPurchaseResponse>>builder()
                .success(true)
                .message("Purchase history fetched successfully")
                .data(libraryService.getPurchaseHistory(
                        SecurityUtils.getCurrentUserId(), page, size))
                .build());
    }

    @GetMapping("/subscription")
    public ResponseEntity<ApiResponse<Optional<LibrarySubscriptionResponse>>> activeSubscription() {
        return ResponseEntity.ok(ApiResponse.<Optional<LibrarySubscriptionResponse>>builder()
                .success(true)
                .message("Active subscription fetched successfully")
                .data(libraryService.getActiveSubscription(SecurityUtils.getCurrentUserId()))
                .build());
    }

    @GetMapping("/downloads")
    public ResponseEntity<ApiResponse<Page<DownloadHistoryResponse>>> downloads(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(ApiResponse.<Page<DownloadHistoryResponse>>builder()
                .success(true)
                .message("Download history fetched successfully")
                .data(libraryService.getDownloadHistory(
                        SecurityUtils.getCurrentUserId(), page, size))
                .build());
    }

    @PostMapping("/resources/{resourceId}/redownload")
    public ResponseEntity<ApiResponse<String>> redownload(@PathVariable Long resourceId) {
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Download link generated successfully")
                .data(libraryService.redownload(
                        SecurityUtils.getCurrentUserId(), resourceId))
                .build());
    }
}
