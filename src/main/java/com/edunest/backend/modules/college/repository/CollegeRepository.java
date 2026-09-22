package com.edunest.backend.modules.college.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edunest.backend.modules.college.entity.College;

@Repository
public interface CollegeRepository extends JpaRepository<College, Long> {

    List<College> findByUniversityId(Long universityId);

    Optional<College> findByCode(String code);
}