package com.edunest.backend.modules.review.repository;

import com.edunest.backend.modules.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @EntityGraph(attributePaths = "user")
    Page<Review> findByResource_Id(Long resourceId, Pageable pageable);

    Optional<Review> findByIdAndResource_Id(Long reviewId, Long resourceId);

    Optional<Review> findByResource_IdAndUser_Id(Long resourceId, Long userId);

    boolean existsByResource_IdAndUser_Id(Long resourceId, Long userId);

    @Query("select avg(r.rating) from Review r where r.resource.id = :resourceId")
    Double findAverageRating(@Param("resourceId") Long resourceId);

    long countByResource_Id(Long resourceId);
}
