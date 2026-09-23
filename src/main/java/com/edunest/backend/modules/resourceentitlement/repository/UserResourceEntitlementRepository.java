package com.edunest.backend.modules.resourceentitlement.repository;

import com.edunest.backend.modules.resourceentitlement.entity.EntitlementSource;
import com.edunest.backend.modules.resourceentitlement.entity.UserResourceEntitlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserResourceEntitlementRepository
        extends JpaRepository<UserResourceEntitlement, Long> {

    @Query("""
            select e
            from UserResourceEntitlement e
            where e.user.id = :userId
              and e.resource.id = :resourceId
              and e.source = :source
              and e.active = true
              and e.startsAt <= :now
              and (e.expiresAt is null or e.expiresAt > :now)
            """)
    Optional<UserResourceEntitlement> findActive(
            @Param("userId") Long userId,
            @Param("resourceId") Long resourceId,
            @Param("source") EntitlementSource source,
            @Param("now") LocalDateTime now);

    Optional<UserResourceEntitlement> findByUser_IdAndResource_IdAndSource(
            Long userId,
            Long resourceId,
            EntitlementSource source);
}
