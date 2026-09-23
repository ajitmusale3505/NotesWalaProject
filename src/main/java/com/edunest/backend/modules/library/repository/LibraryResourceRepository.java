package com.edunest.backend.modules.library.repository;

import com.edunest.backend.modules.resourceentitlement.entity.EntitlementSource;
import com.edunest.backend.modules.resourceentitlement.entity.UserResourceEntitlement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryResourceRepository extends JpaRepository<UserResourceEntitlement, Long> {

    @Query("""
            select e
            from UserResourceEntitlement e
            join e.resource r
            where e.user.id = :userId
              and e.source = :source
              and e.active = true
              and e.startsAt <= :now
              and (e.expiresAt is null or e.expiresAt > :now)
              and r.active = true
              and r.published = true
              and (
                    :keyword is null
                    or lower(r.title) like lower(concat('%', :keyword, '%'))
                    or lower(coalesce(r.description, '')) like lower(concat('%', :keyword, '%'))
                  )
              and (:materialType is null or r.materialType = :materialType)
            order by r.publishedAt desc, r.id desc
            """)
    Page<UserResourceEntitlement> findSubscriptionResources(
            @Param("userId") Long userId,
            @Param("source") com.edunest.backend.modules.resourceentitlement.entity.EntitlementSource source,
            @Param("now") java.time.LocalDateTime now,
            @Param("keyword") String keyword,
            @Param("materialType") com.edunest.backend.common.enums.MaterialType materialType,
            Pageable pageable);
}
