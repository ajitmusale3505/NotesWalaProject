package com.edunest.backend.modules.resource.repository;

import java.util.List;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.edunest.backend.common.enums.AccessType;
import com.edunest.backend.common.enums.MaterialType;
import com.edunest.backend.modules.resource.entity.Resource;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long>, JpaSpecificationExecutor<Resource> {

    // Existing Filters
    boolean existsBySlug(String slug);

    List<Resource> findBySubject_Id(Long subjectId);

    List<Resource> findByBranch_Id(Long branchId);

    List<Resource> findBySemester_Id(Long semesterId);

    List<Resource> findByUniversity_Id(Long universityId);

    List<Resource> findByAcademicYear_Id(Long academicYearId);

    List<Resource> findByMaterialType(MaterialType materialType);

    List<Resource> findByAccessType(AccessType accessType);

    List<Resource> findByTitleContainingIgnoreCase(String keyword);

    List<Resource> findByActiveTrue();

    // Feed API
    List<Resource> findByBranch_IdAndSemester_IdAndActiveTrueAndPublishedTrue(
            Long branchId,
            Long semesterId
    );

    // NEW — Public Resource APIs
    List<Resource> findByActiveTrueAndPublishedTrue();
    Page<Resource> findByActiveTrueAndPublishedTrue(Pageable pageable);

    List<Resource> findByBranch_IdAndActiveTrueAndPublishedTrue(
            Long branchId
    );

    List<Resource> findBySemester_IdAndActiveTrueAndPublishedTrue(
            Long semesterId
    );

    List<Resource> findBySubject_IdAndActiveTrueAndPublishedTrue(
            Long subjectId
    );

    List<Resource> findByTitleContainingIgnoreCaseAndActiveTrueAndPublishedTrue(
            String keyword
    );
    Page<Resource> findByTitleContainingIgnoreCaseAndActiveTrueAndPublishedTrue(String keyword, Pageable pageable);
    
//    @Query("""
//    	    SELECT r FROM Resource r
//    	    WHERE r.active = true
//    	      AND r.published = true
//    	      AND (:keyword IS NULL OR
//    	            LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
//    	            OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
//    	            OR LOWER(r.tags) LIKE LOWER(CONCAT('%', :keyword, '%'))
//    	      )
//    	      AND (:branchId IS NULL OR r.branch.id = :branchId)
//    	      AND (:semesterId IS NULL OR r.semester.id = :semesterId)
//    	      AND (:subjectId IS NULL OR r.subject.id = :subjectId)
//    	      AND (:materialType IS NULL OR r.materialType = :materialType)
//    	""")
//    	List<Resource> filterResources(
//    	        @Param("keyword") String keyword,
//    	        @Param("branchId") Long branchId,
//    	        @Param("semesterId") Long semesterId,
//    	        @Param("subjectId") Long subjectId,
//    	        @Param("materialType") MaterialType materialType
//    	);


    @Modifying
    @Query("""
            update Resource r
               set r.downloadsCount = coalesce(r.downloadsCount, 0) + 1,
                   r.popularityScore = coalesce(r.popularityScore, 0) + 1
             where r.id = :resourceId
            """)
    int incrementDownloads(@Param("resourceId") Long resourceId);

    @Modifying
    @Query("""
            update Resource r
               set r.popularityScore = coalesce(r.popularityScore, 0) + 0.2
             where r.id = :resourceId
            """)
    int incrementViews(@Param("resourceId") Long resourceId);
}
