package com.edunest.backend.modules.bookmark.service.impl;

import com.edunest.backend.modules.bookmark.dto.response.BookmarkResponse;
import com.edunest.backend.modules.bookmark.entity.Bookmark;
import com.edunest.backend.modules.bookmark.repository.BookmarkRepository;
import com.edunest.backend.modules.resource.dto.response.ResourceAccessResponse;
import com.edunest.backend.modules.resource.entity.Resource;
import com.edunest.backend.modules.resource.repository.ResourceRepository;
import com.edunest.backend.modules.resource.service.ResourceService;
import com.edunest.backend.modules.user.entity.User;
import com.edunest.backend.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class BookmarkServiceImplTest {

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ResourceService resourceService;

    @InjectMocks
    private BookmarkServiceImpl bookmarkService;

    private User user;
    private Resource resource;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(20L)
                .fullName("Test User")
                .build();

        resource = Resource.builder()
                .id(10L)
                .title("Test Resource")
                .slug("test-resource")
                .active(true)
                .published(true)
                .downloadable(true)
                .build();
    }

    @Test
    void addBookmarkCreatesBookmark() {
        when(userRepository.findById(20L)).thenReturn(Optional.of(user));
        when(resourceRepository.findById(10L)).thenReturn(Optional.of(resource));
        when(bookmarkRepository.existsByUser_IdAndResource_Id(20L, 10L)).thenReturn(false);
        when(bookmarkRepository.save(any(Bookmark.class))).thenAnswer(invocation -> {
            Bookmark bookmark = invocation.getArgument(0);
            bookmark.setId(1L);
            return bookmark;
        });
        when(resourceService.checkAccess(20L, 10L)).thenReturn(
                ResourceAccessResponse.builder()
                        .allowed(true)
                        .message("Free resource")
                        .canPreview(true)
                        .canViewFull(true)
                        .canDownload(true)
                        .requiresPurchase(false)
                        .viewUrl("/resources/10/view")
                        .downloadUrl("/resources/10/download")
                        .build());

        BookmarkResponse response = bookmarkService.addBookmark(20L, 10L);

        assertEquals(1L, response.getBookmarkId());
        assertEquals(10L, response.getResourceId());
        assertTrue(response.isCanPreview());
        assertTrue(response.isCanViewFull());
        assertEquals("/resources/10/view", response.getViewUrl());
        verify(bookmarkRepository).save(any(Bookmark.class));
    }

    @Test
    void addBookmarkRejectsDuplicate() {
        when(userRepository.findById(20L)).thenReturn(Optional.of(user));
        when(resourceRepository.findById(10L)).thenReturn(Optional.of(resource));
        when(bookmarkRepository.existsByUser_IdAndResource_Id(20L, 10L)).thenReturn(true);

        assertThrows(
                com.edunest.backend.common.exception.BadRequestException.class,
                () -> bookmarkService.addBookmark(20L, 10L));

        verify(bookmarkRepository, never()).save(any());
    }

    @Test
    void removeBookmarkRequiresExistingBookmark() {
        when(bookmarkRepository.findByUser_IdAndResource_Id(20L, 10L))
                .thenReturn(Optional.empty());

        assertThrows(
                com.edunest.backend.common.exception.ResourceNotFoundException.class,
                () -> bookmarkService.removeBookmark(20L, 10L));
    }

    @Test
    void inactiveResourceCannotBeBookmarked() {
        resource.setActive(false);
        when(userRepository.findById(20L)).thenReturn(Optional.of(user));
        when(resourceRepository.findById(10L)).thenReturn(Optional.of(resource));

        assertThrows(
                com.edunest.backend.common.exception.ResourceNotFoundException.class,
                () -> bookmarkService.addBookmark(20L, 10L));

        verify(bookmarkRepository, never()).save(any());
    }
}
