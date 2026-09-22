package com.edunest.backend.modules.university.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edunest.backend.modules.university.entity.University;

@Repository
public interface UniversityRepository
        extends JpaRepository<University, Long> {

    Optional<University> findByShortCode(String shortCode);

    Optional<University> findByName(String name);
}