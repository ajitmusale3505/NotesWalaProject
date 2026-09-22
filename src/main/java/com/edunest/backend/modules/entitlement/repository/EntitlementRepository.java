package com.edunest.backend.modules.entitlement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import com.edunest.backend.common.enums.EntitlementStatus;
import com.edunest.backend.common.enums.EntitlementType;
import com.edunest.backend.modules.entitlement.entity.Entitlement;

@Repository
public interface EntitlementRepository
        extends JpaRepository<Entitlement, Long> {

    List<Entitlement> findByUserId(Long userId);

    List<Entitlement> findByUserIdAndStatus(
            Long userId,
            EntitlementStatus status
    );

    boolean existsByUserIdAndResourceIdAndStatus(
            Long userId,
            Long resourceId,
            EntitlementStatus status
    );

    Optional<Entitlement> findByUserIdAndEntitlementTypeAndStatus(
            Long userId,
            EntitlementType entitlementType,
            EntitlementStatus status
    );

    Optional<Entitlement> findByUserIdAndResourceId(
            Long userId,
            Long resourceId
    );
    
    Optional<Entitlement> findByUserIdAndEntitlementType(
            Long userId,
            EntitlementType entitlementType
    );

    @Modifying
    @org.springframework.data.jpa.repository.Query("""
            update Entitlement e
               set e.remainingAiCredits = e.remainingAiCredits - 1
             where e.user.id = :userId
               and e.entitlementType = :type
               and e.status = :status
               and e.remainingAiCredits > 0
            """)
    int decrementAiCredit(@Param("userId") Long userId,
                          @Param("type") EntitlementType type,
                          @Param("status") EntitlementStatus status);
}