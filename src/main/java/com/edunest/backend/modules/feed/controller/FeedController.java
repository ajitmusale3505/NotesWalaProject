package com.edunest.backend.modules.feed.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.feed.dto.response.FeedResponse;
import com.edunest.backend.modules.feed.service.FeedService;
import com.edunest.backend.security.util.SecurityUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    public ResponseEntity<ApiResponse<FeedResponse>> getFeed() {

        Long userId = SecurityUtils.getCurrentUserId();

        FeedResponse feed = feedService.getFeed(userId);

        ApiResponse<FeedResponse> response =
                ApiResponse.<FeedResponse>builder()
                        .success(true)
                        .message("Feed fetched successfully")
                        .data(feed)
                        .build();

        return ResponseEntity.ok(response);
    }
}