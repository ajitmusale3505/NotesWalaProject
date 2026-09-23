package com.edunest.backend.modules.bookmark.repository;

import com.edunest.backend.modules.bookmark.entity.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Page<Bookmark> findByUser_Id(Long userId, Pageable pageable);

    Optional<Bookmark> findByUser_IdAndResource_Id(Long userId, Long resourceId);

    boolean existsByUser_IdAndResource_Id(Long userId, Long resourceId);
}
