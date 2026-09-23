package com.edunest.backend.modules.bookmark.service.impl;

import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.modules.bookmark.dto.response.BookmarkResponse;
import com.edunest.backend.modules.bookmark.entity.Bookmark;
import com.edunest.backend.modules.bookmark.repository.BookmarkRepository;
import com.edunest.backend.modules.resource.dto.response.ResourceAccessResponse;
import com.edunest.backend.modules.resource.entity.Resource;
import com.edunest.backend.modules.resource.repository.ResourceRepository;
import com.edunest.backend.modules.resource.service.ResourceService;
import com.edunest.backend.modules.user.entity.User;
import com.edunest.backend.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {

    private static final int MAX_PAGE_SIZE = 50;

    private final BookmarkRepository bookmarkRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;
    private final ResourceService resourceService;

    @Override
    @Transactional
    public BookmarkResponse addBookmark(Long userId, Long resourceId) {
        User user = getUser(userId);
        Resource resource = getResource(resourceId);

        ensureResourceAvailable(resource);

        if (bookmarkRepository.existsByUser_IdAndResource_Id(userId, resourceId)) {
            throw new BadRequestException("Resource is already bookmarked");
        }

        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .resource(resource)
                .build();

        return toResponse(bookmarkRepository.save(bookmark), userId);
    }

    @Override
    @Transactional
    public void removeBookmark(Long userId, Long resourceId) {
        requireUserId(userId);

        Bookmark bookmark = bookmarkRepository.findByUser_IdAndResource_Id(userId, resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Bookmark not found"));

        bookmarkRepository.delete(bookmark);
    }

    @Override
    public Page<BookmarkResponse> getMyBookmarks(Long userId, int page, int size) {
        getUser(userId);

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);

        return bookmarkRepository.findByUser_Id(
                        userId,
                        PageRequest.of(
                                safePage,
                                safeSize,
                                Sort.by(
                                        Sort.Order.desc("createdAt"),
                                        Sort.Order.desc("id"))))
                .map(bookmark -> toResponse(bookmark, userId));
    }

    @Override
    public BookmarkResponse getBookmark(Long userId, Long resourceId) {
        requireUserId(userId);

        if (resourceId == null) {
            throw new BadRequestException("Resource is required");
        }

        Bookmark bookmark = bookmarkRepository.findByUser_IdAndResource_Id(userId, resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Bookmark not found"));

        return toResponse(bookmark, userId);
    }

    private BookmarkResponse toResponse(Bookmark bookmark, Long userId) {
        Resource resource = bookmark.getResource();
        ResourceAccessResponse access = resolveAccess(userId, resource);

        return BookmarkResponse.builder()
                .bookmarkId(bookmark.getId())
                .resourceId(resource.getId())
                .title(resource.getTitle())
                .slug(resource.getSlug())
                .materialType(resource.getMaterialType())
                .accessType(resource.getAccessType())
                .thumbnailUrl(resource.getThumbnailUrl())
                .coverImageUrl(resource.getCoverImageUrl())
                .ratingAverage(resource.getRatingAverage())
                .ratingCount(resource.getRatingCount())
                .bookmarkedAt(bookmark.getCreatedAt())
                .resourceActive(resource.isActive())
                .resourcePublished(resource.isPublished())
                .canPreview(access.isCanPreview())
                .canViewFull(access.isCanViewFull())
                .canDownload(access.isCanDownload())
                .requiresPurchase(access.isRequiresPurchase())
                .accessMessage(access.getMessage())
                .viewUrl(access.getViewUrl())
                .downloadUrl(access.getDownloadUrl())
                .build();
    }

    private ResourceAccessResponse resolveAccess(Long userId, Resource resource) {
        if (!resource.isActive() || !resource.isPublished()) {
            return ResourceAccessResponse.builder()
                    .allowed(false)
                    .message("Resource is not currently available")
                    .canPreview(false)
                    .canViewFull(false)
                    .canDownload(false)
                    .requiresPurchase(false)
                    .build();
        }

        return resourceService.checkAccess(userId, resource.getId());
    }

    private void ensureResourceAvailable(Resource resource) {
        if (!resource.isActive() || !resource.isPublished()) {
            throw new ResourceNotFoundException("Resource not available");
        }
    }

    private User getUser(Long userId) {
        requireUserId(userId);

        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    private Resource getResource(Long resourceId) {
        if (resourceId == null) {
            throw new BadRequestException("Resource is required");
        }

        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
    }

    private void requireUserId(Long userId) {
        if (userId == null) {
            throw new AccessDeniedException("Authenticated user required");
        }
    }
}
