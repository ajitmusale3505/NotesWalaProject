package com.edunest.backend.modules.college.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edunest.backend.modules.college.entity.College;

@Repository
public interface CollegeRepository extends JpaRepository<College, Long> {

    @Override
    @EntityGraph(attributePaths = {"university"})
    List<College> findAll();

    @Override
    @EntityGraph(attributePaths = {"university"})
    Optional<College> findById(Long id);

    @EntityGraph(attributePaths = {"university"})
    List<College> findByUniversityId(Long universityId);

    @EntityGraph(attributePaths = {"university"})
    Optional<College> findByCode(String code);
}