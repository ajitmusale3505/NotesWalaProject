package com.edunest.backend.modules.library.repository;

import com.edunest.backend.common.enums.AnalyticsEventType;
import com.edunest.backend.modules.resourceanalytics.entity.ResourceAnalytics;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryDownloadHistoryRepository extends JpaRepository<ResourceAnalytics, Long> {

    @Query("""
            select a
            from ResourceAnalytics a
            join fetch a.resource r
            where a.user.id = :userId
              and a.eventType = :eventType
            order by a.createdAt desc, a.id desc
            """)
    Page<ResourceAnalytics> findDownloadHistory(
            @Param("userId") Long userId,
            @Param("eventType") AnalyticsEventType eventType,
            Pageable pageable);
}
