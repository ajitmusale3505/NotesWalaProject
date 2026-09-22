package com.edunest.backend.modules.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edunest.backend.common.enums.OrderStatus;
import com.edunest.backend.modules.order.entity.Order;
import com.edunest.backend.modules.order.entity.OrderItem;

@Repository
public interface OrderItemRepository
        extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder(Order order);

    boolean existsByOrderUserIdAndResourceIdAndOrderStatus(
            Long userId,
            Long resourceId,
            OrderStatus status);
}