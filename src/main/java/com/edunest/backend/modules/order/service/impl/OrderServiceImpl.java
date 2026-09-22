package com.edunest.backend.modules.order.service.impl;

import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.common.exception.ResourceNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.edunest.backend.common.enums.OrderStatus;
import com.edunest.backend.common.enums.OrderType;
import com.edunest.backend.common.enums.PaymentProvider;
import com.edunest.backend.common.enums.PaymentStatus;
import com.edunest.backend.modules.order.dto.request.CreateOrderRequest;
import com.edunest.backend.modules.order.dto.response.OrderResponse;
import com.edunest.backend.modules.order.entity.Order;
import com.edunest.backend.modules.order.entity.OrderItem;
import com.edunest.backend.modules.order.repository.OrderItemRepository;
import com.edunest.backend.modules.order.repository.OrderRepository;
import com.edunest.backend.modules.order.service.OrderService;
import com.edunest.backend.modules.payment.entity.Payment;
import com.edunest.backend.modules.payment.repository.PaymentRepository;
import com.edunest.backend.modules.resource.entity.Resource;
import com.edunest.backend.modules.resource.repository.ResourceRepository;
import com.edunest.backend.modules.user.entity.User;
import com.edunest.backend.modules.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import com.edunest.backend.common.enums.AccessType;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Resource resource = resourceRepository.findById(request.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        if (!resource.isActive() || !resource.isPublished()) {
            throw new BadRequestException("Resource is not available for purchase");
        }

        if (resource.getAccessType() == AccessType.FREE) {
            throw new BadRequestException("Free resources do not require an order");
        }

        if (hasPurchased(user.getId(), resource.getId())) {
            throw new BadRequestException("Already purchased");
        }

        BigDecimal price = resource.getDiscountPrice() != null
                ? resource.getDiscountPrice()
                : resource.getPrice();

        if (price == null || price.signum() < 0) {
            throw new BadRequestException("Resource has invalid pricing");
        }

        Order order = Order.builder()
                .orderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8))
                .user(user)
                .orderType(OrderType.RESOURCE)
                .subtotalAmount(price)
                .discountAmount(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .finalAmount(price)
                .currency("INR")
                .status(OrderStatus.PENDING_PAYMENT)
                .invoiceGenerated(false)
                .build();

        order = orderRepository.save(order);

        OrderItem item = OrderItem.builder()
                .order(order)
                .resource(resource)
                .resourceTitleSnapshot(resource.getTitle())
                .materialTypeSnapshot(resource.getMaterialType())
                .unitPrice(resource.getPrice())
                .discountAmount(BigDecimal.ZERO)
                .finalUnitPrice(price)
                .quantity(1)
                .lineTotal(price)
                .pageCountSnapshot(resource.getPageCount())
                .fileSizeBytesSnapshot(resource.getFileSizeBytes())
                .build();

        orderItemRepository.save(item);

        Payment payment = Payment.builder()
                .order(order)
                .provider(PaymentProvider.RAZORPAY)
                .amount(price)
                .currency("INR")
                .status(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);

        return OrderResponse.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .orderType(order.getOrderType())
                .status(order.getStatus())
                .subtotalAmount(order.getSubtotalAmount())
                .discountAmount(order.getDiscountAmount())
                .taxAmount(order.getTaxAmount())
                .finalAmount(order.getFinalAmount())
                .resourceTitle(resource.getTitle())
                .amount(price)
                .currency(order.getCurrency())
                .paymentRequired(true)
                .build();
    }

    @Override
    public List<OrderResponse> getOrdersByUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        return orderRepository.findByUser(user)
                .stream()
                .map(order -> OrderResponse.builder()
                        .orderId(order.getId())
                        .orderNumber(order.getOrderNumber())
                        .status(order.getStatus())
                        .amount(order.getFinalAmount())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasPurchased(Long userId, Long resourceId) {
        return orderItemRepository
                .existsByOrderUserIdAndResourceIdAndOrderStatus(
                        userId,
                        resourceId,
                        OrderStatus.PAID
                );
    }
}