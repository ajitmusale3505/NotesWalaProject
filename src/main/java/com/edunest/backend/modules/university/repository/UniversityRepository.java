package com.edunest.backend.modules.university.repository;

import com.edunest.backend.modules.university.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UniversityRepository extends JpaRepository<University, Long> {
    List<University> findAllByActiveTrue();
    Optional<University> findByIdAndActiveTrue(Long id);
    Optional<University> findByShortCode(String shortCode);
    Optional<University> findByName(String name);
}