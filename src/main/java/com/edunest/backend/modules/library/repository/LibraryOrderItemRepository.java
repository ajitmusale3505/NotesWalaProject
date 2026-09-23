package com.edunest.backend.modules.library.repository;

import com.edunest.backend.common.enums.OrderStatus;
import com.edunest.backend.modules.order.entity.OrderItem;
import com.edunest.backend.modules.resourceentitlement.entity.EntitlementSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface LibraryOrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query(value = """
            select distinct oi
            from OrderItem oi
            join fetch oi.order o
            join fetch oi.resource r
            join UserResourceEntitlement e
              on e.user.id = :userId
             and e.resource.id = r.id
             and e.source = :source
             and e.active = true
             and e.startsAt <= :now
             and (e.expiresAt is null or e.expiresAt > :now)
            where o.user.id = :userId
              and o.status = :status
              and (
                    :keyword is null
                    or lower(oi.resourceTitleSnapshot) like lower(concat('%', :keyword, '%'))
                    or lower(r.title) like lower(concat('%', :keyword, '%'))
                  )
              and (:materialType is null or r.materialType = :materialType)
            order by o.paidAt desc, oi.id desc
            """,
            countQuery = """
            select count(oi)
            from OrderItem oi
            join oi.order o
            join oi.resource r
            join UserResourceEntitlement e
              on e.user.id = :userId
             and e.resource.id = r.id
             and e.source = :source
             and e.active = true
             and e.startsAt <= :now
             and (e.expiresAt is null or e.expiresAt > :now)
            where o.user.id = :userId
              and o.status = :status
              and (
                    :keyword is null
                    or lower(oi.resourceTitleSnapshot) like lower(concat('%', :keyword, '%'))
                    or lower(r.title) like lower(concat('%', :keyword, '%'))
                  )
              and (:materialType is null or r.materialType = :materialType)
            """)
    Page<OrderItem> findPurchasedResources(
            @Param("userId") Long userId,
            @Param("status") OrderStatus status,
            @Param("source") EntitlementSource source,
            @Param("now") LocalDateTime now,
            @Param("keyword") String keyword,
            @Param("materialType") com.edunest.backend.common.enums.MaterialType materialType,
            Pageable pageable);

    List<OrderItem> findByOrder_IdIn(Collection<Long> orderIds);
}
