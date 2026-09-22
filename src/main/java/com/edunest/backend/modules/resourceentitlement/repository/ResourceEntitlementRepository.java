package com.edunest.backend.modules.resourceentitlement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edunest.backend.modules.resourceentitlement.entity.ResourceEntitlement;

@Repository
public interface ResourceEntitlementRepository
        extends JpaRepository<ResourceEntitlement, Long> {

    List<ResourceEntitlement> findByResource_Id(Long resourceId);

    boolean existsByResource_IdAndSubscriptionPlan_Id(
            Long resourceId,
            Long subscriptionPlanId
    );
}