package com.edunest.backend.modules.library.repository;

import com.edunest.backend.common.enums.OrderStatus;
import com.edunest.backend.modules.order.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryOrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("""
            select oi
            from OrderItem oi
            join fetch oi.order o
            join fetch oi.resource r
            left join fetch r.category
            left join fetch r.subject
            left join fetch r.branch
            left join fetch r.semester
            where o.user.id = :userId
              and o.status = :status
              and (
                    :keyword is null
                    or lower(oi.resourceTitleSnapshot) like lower(concat('%', :keyword, '%'))
                    or lower(r.title) like lower(concat('%', :keyword, '%'))
                  )
              and (:materialType is null or r.materialType = :materialType)
            order by o.paidAt desc, oi.id desc
            """)
    Page<OrderItem> findPurchasedResources(
            @Param("userId") Long userId,
            @Param("status") OrderStatus status,
            @Param("keyword") String keyword,
            @Param("materialType") com.edunest.backend.common.enums.MaterialType materialType,
            Pageable pageable);

    @Query("""
            select oi
            from OrderItem oi
            join fetch oi.order o
            join fetch oi.resource r
            where o.user.id = :userId
              and o.status = :status
            order by o.paidAt desc, oi.id desc
            """)
    java.util.List<OrderItem> findPurchasedResources(
            @Param("userId") Long userId,
            @Param("status") OrderStatus status);
}
