package com.edunest.backend.modules.semester.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edunest.backend.modules.semester.entity.Semester;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {

    @Override
    @EntityGraph(attributePaths = {"academicYear"})
    List<Semester> findAll();

    @Override
    @EntityGraph(attributePaths = {"academicYear"})
    Optional<Semester> findById(Long id);

    @EntityGraph(attributePaths = {"academicYear"})
    List<Semester> findByAcademicYearId(Long academicYearId);

    @EntityGraph(attributePaths = {"academicYear"})
    Optional<Semester> findByNumberAndAcademicYear_Code(Integer number, String code);
}