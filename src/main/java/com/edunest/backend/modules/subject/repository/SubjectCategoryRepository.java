package com.edunest.backend.modules.subject.repository;

import com.edunest.backend.modules.subject.entity.SubjectCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectCategoryRepository extends JpaRepository<SubjectCategory, String> {
    List<SubjectCategory> findByActiveTrueOrderByNameAsc();
    Optional<SubjectCategory> findByCodeIgnoreCase(String code);
}
