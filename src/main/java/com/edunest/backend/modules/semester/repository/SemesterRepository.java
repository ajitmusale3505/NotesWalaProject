package com.edunest.backend.modules.semester.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edunest.backend.modules.semester.entity.Semester;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {

    List<Semester> findByAcademicYearId(Long academicYearId);

    Optional<Semester> findByNumberAndAcademicYear_Code(Integer number, String code);
}