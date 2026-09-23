package com.edunest.backend.modules.library.repository;

import com.edunest.backend.modules.resource.entity.Resource;
import com.edunest.backend.modules.resourceentitlement.entity.EntitlementSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryResourceRepository extends JpaRepository<Resource, Long> {

    @Query("""
            select r
            from ResourceEntitlement re
            join re.resource r
            where re.subscriptionPlan.id = :planId
              and re.active = true
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
    Page<Resource> findSubscriptionResources(
            @Param("planId") Long planId,
            @Param("keyword") String keyword,
            @Param("materialType") com.edunest.backend.common.enums.MaterialType materialType,
            Pageable pageable);
}
