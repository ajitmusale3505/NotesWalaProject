package com.edunest.backend.modules.feed.service;

import com.edunest.backend.modules.feed.dto.response.FeedResponse;

public interface FeedService {

    FeedResponse getFeed(Long userId);
}