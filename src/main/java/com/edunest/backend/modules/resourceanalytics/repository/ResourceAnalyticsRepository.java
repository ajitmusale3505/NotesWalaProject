package com.edunest.backend.modules.resourceanalytics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edunest.backend.modules.resourceanalytics.entity.ResourceAnalytics;

@Repository
public interface ResourceAnalyticsRepository
        extends JpaRepository<ResourceAnalytics, Long> {
}