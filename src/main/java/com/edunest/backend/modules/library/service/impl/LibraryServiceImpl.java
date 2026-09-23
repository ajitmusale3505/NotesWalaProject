package com.edunest.backend.modules.library.service.impl;

import com.edunest.backend.common.enums.AnalyticsEventType;
import com.edunest.backend.common.enums.MaterialType;
import com.edunest.backend.common.enums.OrderStatus;
import com.edunest.backend.common.enums.SubscriptionStatus;
import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.modules.library.dto.response.*;
import com.edunest.backend.modules.library.repository.*;
import com.edunest.backend.modules.library.service.LibraryService;
import com.edunest.backend.modules.order.entity.Order;
import com.edunest.backend.modules.order.entity.OrderItem;
import com.edunest.backend.modules.resource.entity.Resource;
import com.edunest.backend.modules.resource.service.ResourceService;
import com.edunest.backend.modules.resourceentitlement.entity.EntitlementSource;
import com.edunest.backend.modules.resourceentitlement.entity.UserResourceEntitlement;
import com.edunest.backend.modules.subscription.entity.Subscription;
import com.edunest.backend.modules.subscription.repository.SubscriptionRepository;
import com.edunest.backend.modules.user.entity.User;
import com.edunest.backend.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LibraryServiceImpl implements LibraryService {

    private final LibraryOrderItemRepository orderItemRepository;
    private final LibraryOrderRepository orderRepository;
    private final LibraryResourceRepository resourceRepository;
    private final LibraryDownloadHistoryRepository downloadHistoryRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final ResourceService resourceService;

    @Override
    public Page<LibraryResourceResponse> getPurchasedResources(Long userId, String keyword, MaterialType materialType, int page, int size) {
        getUser(userId);
        return orderItemRepository.findPurchasedResources(
                userId, OrderStatus.PAID, EntitlementSource.PURCHASE, LocalDateTime.now(),
                normalizeKeyword(keyword), materialType, pageable(page, size))
                .map(this::mapPurchasedResource);
    }

    @Override
    public Page<LibraryResourceResponse> getSubscriptionResources(Long userId, String keyword, MaterialType materialType, int page, int size) {
        getUser(userId);
        return resourceRepository.findSubscriptionResources(
                userId, EntitlementSource.SUBSCRIPTION, LocalDateTime.now(),
                normalizeKeyword(keyword), materialType, pageable(page, size))
                .map(this::mapSubscriptionResource);
    }

    @Override
    public Page<LibraryPurchaseResponse> getPurchaseHistory(Long userId, int page, int size) {
        getUser(userId);
        Page<Order> orders = orderRepository.findPurchaseHistory(userId, pageable(page, size));
        if (orders.isEmpty()) {
            return orders.map(order -> LibraryPurchaseResponse.builder().build());
        }

        List<Long> orderIds = orders.getContent().stream().map(Order::getId).toList();
        Map<Long, List<OrderItem>> itemsByOrder = orderItemRepository.findByOrder_IdIn(orderIds)
                .stream().collect(Collectors.groupingBy(item -> item.getOrder().getId()));

        return orders.map(order -> mapPurchaseHistory(order, itemsByOrder.getOrDefault(order.getId(), List.of())));
    }

    @Override
    public Optional<LibrarySubscriptionResponse> getActiveSubscription(Long userId) {
        User user = getUser(userId);
        return subscriptionRepository.findByUserAndStatus(user, SubscriptionStatus.ACTIVE)
                .filter(subscription -> subscription.getEndDate() != null
                        && subscription.getEndDate().isAfter(LocalDateTime.now()))
                .map(this::mapSubscription);
    }

    @Override
    public Page<DownloadHistoryResponse> getDownloadHistory(Long userId, int page, int size) {
        getUser(userId);
        return downloadHistoryRepository.findDownloadHistory(
                userId, AnalyticsEventType.DOWNLOAD, pageable(page, size))
                .map(analytics -> {
                    Resource resource = analytics.getResource();
                    return DownloadHistoryResponse.builder()
                            .resourceId(resource.getId())
                            .title(resource.getTitle())
                            .materialType(resource.getMaterialType())
                            .materialDisplayName(displayName(resource.getMaterialType()))
                            .downloadedAt(analytics.getCreatedAt())
                            .build();
                });
    }

    @Override
    @Transactional
    public String redownload(Long userId, Long resourceId) {
        getUser(userId);
        return resourceService.generateDownloadUrl(userId, resourceId);
    }

    private LibraryResourceResponse mapPurchasedResource(OrderItem item) {
        Resource resource = item.getResource();
        boolean available = resource.isActive() && resource.isPublished();

        return LibraryResourceResponse.builder()
                .resourceId(resource.getId())
                .title(item.getResourceTitleSnapshot())
                .slug(resource.getSlug())
                .materialType(resource.getMaterialType())
                .materialDisplayName(displayName(resource.getMaterialType()))
                .categoryName(resource.getCategory() != null ? resource.getCategory().getName() : null)
                .subjectName(resource.getSubject() != null ? resource.getSubject().getName() : null)
                .branchName(resource.getBranch() != null ? resource.getBranch().getName() : null)
                .semesterNumber(resource.getSemester() != null ? resource.getSemester().getNumber() : null)
                .accessType(resource.getAccessType())
                .price(resource.getPrice())
                .purchasedPrice(item.getFinalUnitPrice())
                .thumbnailUrl(resource.getThumbnailUrl())
                .coverImageUrl(resource.getCoverImageUrl())
                .canPreview(available)
                .canViewFull(available)
                .canDownload(available && resource.isDownloadable())
                .accessStatus(available ? "ACTIVE" : "UNAVAILABLE")
                .orderId(item.getOrder().getId())
                .orderNumber(item.getOrder().getOrderNumber())
                .purchasedAt(item.getOrder().getPaidAt() != null
                        ? item.getOrder().getPaidAt() : item.getOrder().getCreatedAt())
                .build();
    }

    private LibraryResourceResponse mapSubscriptionResource(UserResourceEntitlement entitlement) {
        Resource resource = entitlement.getResource();
        boolean available = resource.isActive() && resource.isPublished();

        return LibraryResourceResponse.builder()
                .resourceId(resource.getId())
                .title(resource.getTitle())
                .slug(resource.getSlug())
                .materialType(resource.getMaterialType())
                .materialDisplayName(displayName(resource.getMaterialType()))
                .categoryName(resource.getCategory() != null ? resource.getCategory().getName() : null)
                .subjectName(resource.getSubject() != null ? resource.getSubject().getName() : null)
                .branchName(resource.getBranch() != null ? resource.getBranch().getName() : null)
                .semesterNumber(resource.getSemester() != null ? resource.getSemester().getNumber() : null)
                .accessType(resource.getAccessType())
                .price(resource.getPrice())
                .thumbnailUrl(resource.getThumbnailUrl())
                .coverImageUrl(resource.getCoverImageUrl())
                .canPreview(available)
                .canViewFull(available)
                .canDownload(available && resource.isDownloadable())
                .accessStatus(available ? "ACTIVE" : "UNAVAILABLE")
                .entitlementExpiresAt(entitlement.getExpiresAt())
                .build();
    }

    private LibraryPurchaseResponse mapPurchaseHistory(Order order, List<OrderItem> items) {
        return LibraryPurchaseResponse.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .orderType(order.getOrderType())
                .status(order.getStatus())
                .amount(order.getFinalAmount())
                .currency(order.getCurrency())
                .purchasedAt(order.getPaidAt() != null ? order.getPaidAt() : order.getCreatedAt())
                .items(items.stream().map(item -> LibraryPurchaseItemResponse.builder()
                        .resourceId(item.getResource().getId())
                        .title(item.getResourceTitleSnapshot())
                        .materialType(item.getMaterialTypeSnapshot())
                        .materialDisplayName(displayName(item.getMaterialTypeSnapshot()))
                        .amount(item.getLineTotal())
                        .quantity(item.getQuantity())
                        .build()).toList())
                .build();
    }

    private LibrarySubscriptionResponse mapSubscription(Subscription subscription) {
        return LibrarySubscriptionResponse.builder()
                .subscriptionId(subscription.getId())
                .planId(subscription.getPlan().getId())
                .planName(subscription.getPlan().getName())
                .status(subscription.getStatus())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .active(true)
                .build();
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Pageable pageable(int page, int size) {
        return PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50));
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null) return null;
        String normalized = keyword.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private String displayName(MaterialType materialType) {
        return materialType == null ? null : materialType.getDisplayName();
    }
}
