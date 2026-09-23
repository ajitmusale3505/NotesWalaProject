package com.edunest.backend.modules.bookmark.service;

import com.edunest.backend.modules.bookmark.dto.response.BookmarkResponse;
import org.springframework.data.domain.Page;

public interface BookmarkService {

    BookmarkResponse addBookmark(Long userId, Long resourceId);

    void removeBookmark(Long userId, Long resourceId);

    Page<BookmarkResponse> getMyBookmarks(Long userId, int page, int size);

    BookmarkResponse getBookmark(Long userId, Long resourceId);
}
