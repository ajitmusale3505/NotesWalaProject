package com.edunest.backend.modules.library.repository;

import com.edunest.backend.modules.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryOrderRepository extends JpaRepository<Order, Long> {

    @Query("""
            select o
            from Order o
            where o.user.id = :userId
            order by o.createdAt desc, o.id desc
            """)
    Page<Order> findPurchaseHistory(
            @Param("userId") Long userId,
            Pageable pageable);
}
