package com.edunest.backend.modules.order.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.edunest.backend.modules.order.dto.request.CreateOrderRequest;
import com.edunest.backend.modules.order.dto.response.OrderResponse;
import com.edunest.backend.modules.order.service.OrderService;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import com.edunest.backend.security.util.SecurityUtils;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderResponse createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        // The authenticated identity is authoritative; never trust request.userId.
        request.setUserId(SecurityUtils.getCurrentUserId());
        return orderService.createOrder(request);
    }

    @GetMapping("/user/{userId}")
    public List<OrderResponse> getUserOrders(
            @PathVariable Long userId) {
        SecurityUtils.requireSameUserOrAdmin(userId);
        return orderService.getOrdersByUser(userId);
    }

    @GetMapping("/check")
    public boolean checkPurchased(
            @RequestParam Long userId,
            @RequestParam Long resourceId) {

        SecurityUtils.requireSameUserOrAdmin(userId);
        return orderService.hasPurchased(userId, resourceId);
    }
}