package com.edunest.backend.modules.library.repository;

import com.edunest.backend.common.enums.OrderStatus;
import com.edunest.backend.modules.order.entity.Order;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryOrderRepository extends JpaRepository<Order, Long> {

    @Query("""
            select distinct o
            from Order o
            left join fetch o.items i
            left join fetch i.resource r
            where o.user.id = :userId
            order by o.createdAt desc, o.id desc
            """)
    Page<Order> findPurchaseHistory(
            @Param("userId") Long userId,
            Pageable pageable);
}
