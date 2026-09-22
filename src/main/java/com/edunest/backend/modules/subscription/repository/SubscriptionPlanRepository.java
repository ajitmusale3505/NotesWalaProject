package com.edunest.backend.modules.subscription.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edunest.backend.modules.subscription.entity.SubscriptionPlan;

@Repository
public interface SubscriptionPlanRepository
        extends JpaRepository<SubscriptionPlan, Long> {

    Optional<SubscriptionPlan> findBySlug(String slug);

    Optional<SubscriptionPlan> findByName(String name);
}