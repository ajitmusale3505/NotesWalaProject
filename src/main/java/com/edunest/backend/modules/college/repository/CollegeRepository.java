package com.edunest.backend.modules.college.repository;

import com.edunest.backend.modules.college.entity.College;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CollegeRepository extends JpaRepository<College, Long> {
    @EntityGraph(attributePaths = {"university"})
    List<College> findAllByActiveTrue();
    @EntityGraph(attributePaths = {"university"})
    Optional<College> findByIdAndActiveTrue(Long id);
    @EntityGraph(attributePaths = {"university"})
    List<College> findByUniversityIdAndActiveTrue(Long universityId);
    @EntityGraph(attributePaths = {"university"})
    Optional<College> findByCode(String code);
}