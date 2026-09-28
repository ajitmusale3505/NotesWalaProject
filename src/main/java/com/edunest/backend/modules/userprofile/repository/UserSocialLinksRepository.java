package com.edunest.backend.modules.userprofile.repository;

import com.edunest.backend.modules.userprofile.entity.UserSocialLinks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSocialLinksRepository extends JpaRepository<UserSocialLinks, Long> {

    Optional<UserSocialLinks> findByUserId(Long userId);
}
