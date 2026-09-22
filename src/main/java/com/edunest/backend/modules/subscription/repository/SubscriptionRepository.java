package com.edunest.backend.modules.subscription.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edunest.backend.common.enums.SubscriptionStatus;
import com.edunest.backend.modules.subscription.entity.Subscription;
import com.edunest.backend.modules.user.entity.User;

@Repository
public interface SubscriptionRepository
        extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByUserAndStatus(
            User user,
            SubscriptionStatus status);

    List<Subscription> findByUser(User user);
}