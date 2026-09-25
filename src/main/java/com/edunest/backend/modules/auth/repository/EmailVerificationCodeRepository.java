package com.edunest.backend.modules.auth.repository;

import com.edunest.backend.modules.auth.entity.EmailVerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationCodeRepository extends JpaRepository<EmailVerificationCode, Long> {

    Optional<EmailVerificationCode> findTopByEmailAndPurposeOrderByCreatedAtDesc(String email, String purpose);

    void deleteByEmailAndPurpose(String email, String purpose);
}
