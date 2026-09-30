package com.edunest.backend.modules.program.repository;

import com.edunest.backend.modules.program.entity.Program;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramRepository extends JpaRepository<Program, String> {

    @EntityGraph(attributePaths = "university")
    List<Program> findAllByActiveTrueOrderByNameAsc();

    @EntityGraph(attributePaths = "university")
    Optional<Program> findByIdAndActiveTrue(String id);

    boolean existsByUniversityIdAndCodeIgnoreCase(Long universityId, String code);
}
