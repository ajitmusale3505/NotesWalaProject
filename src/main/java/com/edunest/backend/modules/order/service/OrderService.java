package com.edunest.backend.modules.order.service;

import java.util.List;

import com.edunest.backend.modules.order.dto.request.CreateOrderRequest;
import com.edunest.backend.modules.order.dto.response.OrderResponse;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request);

    List<OrderResponse> getOrdersByUser(Long userId);

    boolean hasPurchased(Long userId, Long resourceId);
}