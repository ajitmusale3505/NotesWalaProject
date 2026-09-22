package com.edunest.backend.modules.year.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edunest.backend.modules.year.entity.AcademicYear;

@Repository
public interface AcademicYearRepository
        extends JpaRepository<AcademicYear, Long> {

    Optional<AcademicYear> findByCode(String code);
}