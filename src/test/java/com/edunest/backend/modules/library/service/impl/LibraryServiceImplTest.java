package com.edunest.backend.modules.library.service.impl;

import com.edunest.backend.common.enums.MaterialType;
import com.edunest.backend.modules.library.repository.*;
import com.edunest.backend.modules.resource.service.ResourceService;
import com.edunest.backend.modules.subscription.repository.SubscriptionRepository;
import com.edunest.backend.modules.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceImplTest {

    @Mock private LibraryOrderItemRepository orderItemRepository;
    @Mock private LibraryOrderRepository orderRepository;
    @Mock private LibraryResourceRepository resourceRepository;
    @Mock private LibraryDownloadHistoryRepository downloadHistoryRepository;
    @Mock private SubscriptionRepository subscriptionRepository;
    @Mock private UserRepository userRepository;
    @Mock private ResourceService resourceService;

    @InjectMocks
    private LibraryServiceImpl libraryService;

    @Test
    void redownloadDelegatesAccessValidationToResourceService() {
        when(userRepository.findById(10L))
                .thenReturn(java.util.Optional.of(new com.edunest.backend.modules.user.entity.User()));
        when(resourceService.generateDownloadUrl(10L, 20L))
                .thenReturn("signed-download-url");

        assertEquals("signed-download-url", libraryService.redownload(10L, 20L));

        verify(resourceService).generateDownloadUrl(10L, 20L);
    }

    @Test
    void purchasedResourcesNormalizeBlankKeyword() {
        when(userRepository.findById(10L))
                .thenReturn(java.util.Optional.of(new com.edunest.backend.modules.user.entity.User()));
        when(orderItemRepository.findPurchasedResources(
                eq(10L), any(), any(), any(), isNull(), eq(MaterialType.BOOK_PDF), any()))
                .thenReturn(org.springframework.data.domain.Page.empty());

        libraryService.getPurchasedResources(10L, "   ", MaterialType.BOOK_PDF, 0, 100);

        ArgumentCaptor<org.springframework.data.domain.Pageable> pageable =
                ArgumentCaptor.forClass(org.springframework.data.domain.Pageable.class);
        verify(orderItemRepository).findPurchasedResources(
                eq(10L), any(), any(), any(), isNull(), eq(MaterialType.BOOK_PDF), pageable.capture());

        assertEquals(50, pageable.getValue().getPageSize());
    }
}
