package com.edunest.backend.modules.bookmark.controller;

import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.bookmark.dto.response.BookmarkResponse;
import com.edunest.backend.modules.bookmark.service.BookmarkService;
import com.edunest.backend.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/{resourceId}")
    public ResponseEntity<ApiResponse<BookmarkResponse>> addBookmark(
            @PathVariable Long resourceId) {

        BookmarkResponse bookmark = bookmarkService.addBookmark(
                SecurityUtils.getCurrentUserId(), resourceId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<BookmarkResponse>builder()
                        .success(true)
                        .message("Resource bookmarked successfully")
                        .data(bookmark)
                        .build());
    }

    @DeleteMapping("/{resourceId}")
    public ResponseEntity<ApiResponse<Void>> removeBookmark(
            @PathVariable Long resourceId) {

        bookmarkService.removeBookmark(
                SecurityUtils.getCurrentUserId(), resourceId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Bookmark removed successfully")
                        .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BookmarkResponse>>> getMyBookmarks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(
                ApiResponse.<Page<BookmarkResponse>>builder()
                        .success(true)
                        .message("Bookmarks fetched successfully")
                        .data(bookmarkService.getMyBookmarks(
                                SecurityUtils.getCurrentUserId(), page, size))
                        .build());
    }

    @GetMapping("/{resourceId}")
    public ResponseEntity<ApiResponse<BookmarkResponse>> getBookmark(
            @PathVariable Long resourceId) {

        return ResponseEntity.ok(
                ApiResponse.<BookmarkResponse>builder()
                        .success(true)
                        .message("Bookmark fetched successfully")
                        .data(bookmarkService.getBookmark(
                                SecurityUtils.getCurrentUserId(), resourceId))
                        .build());
    }
}
