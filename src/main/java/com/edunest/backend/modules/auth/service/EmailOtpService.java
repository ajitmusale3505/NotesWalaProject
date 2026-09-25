package com.edunest.backend.modules.auth.service;

import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.modules.auth.entity.EmailVerificationCode;
import com.edunest.backend.modules.auth.repository.EmailVerificationCodeRepository;
import com.edunest.backend.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HexFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class EmailOtpService {

    private static final int OTP_EXPIRY_MINUTES = 10;
    private static final int MAX_ATTEMPTS = 5;
    private static final int RESEND_COOLDOWN_SECONDS = 60;

    private final EmailVerificationCodeRepository repository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    @Transactional
    public void sendOtp(String rawEmail, String purpose) {
        String email = normalizeEmail(rawEmail);
        String normalizedPurpose = normalizePurpose(purpose);

        boolean registered = userRepository.existsByEmail(email);
        if ("REGISTER".equals(normalizedPurpose) && registered) {
            throw new BadRequestException("Email is already registered");
        }
        if ("LOGIN".equals(normalizedPurpose) && !registered) {
            throw new BadRequestException("No account exists for this email");
        }

        repository.findTopByEmailAndPurposeOrderByCreatedAtDesc(email, normalizedPurpose)
                .filter(existing -> existing.getCreatedAt().isAfter(
                        LocalDateTime.now(ZoneOffset.UTC).minusSeconds(RESEND_COOLDOWN_SECONDS)))
                .ifPresent(existing -> {
                    throw new BadRequestException("Please wait before requesting another OTP");
                });

        repository.deleteByEmailAndPurpose(email, normalizedPurpose);

        String otp = String.format("%06d", new SecureRandom().nextInt(1_000_000));
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        repository.save(EmailVerificationCode.builder()
                .email(email)
                .purpose(normalizedPurpose)
                .codeHash(sha256(otp))
                .expiresAt(now.plusMinutes(OTP_EXPIRY_MINUTES))
                .attempts(0)
                .createdAt(now)
                .build());

        sendEmail(email, otp, normalizedPurpose);
    }

    @Transactional
    public void verifyOtp(String rawEmail, String purpose, String otp) {
        String email = normalizeEmail(rawEmail);
        String normalizedPurpose = normalizePurpose(purpose);

        EmailVerificationCode record = repository
                .findTopByEmailAndPurposeOrderByCreatedAtDesc(email, normalizedPurpose)
                .orElseThrow(() -> new BadRequestException("OTP not found. Please request a new OTP"));

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        if (record.getVerifiedAt() != null) {
            return;
        }
        if (record.getExpiresAt().isBefore(now)) {
            repository.delete(record);
            throw new BadRequestException("OTP has expired. Please request a new OTP");
        }
        if (record.getAttempts() >= MAX_ATTEMPTS) {
            repository.delete(record);
            throw new BadRequestException("Too many incorrect attempts. Please request a new OTP");
        }

        if (!MessageDigest.isEqual(
                record.getCodeHash().getBytes(StandardCharsets.UTF_8),
                sha256(otp).getBytes(StandardCharsets.UTF_8))) {
            record.setAttempts(record.getAttempts() + 1);
            repository.save(record);
            throw new BadRequestException("Invalid OTP");
        }

        record.setVerifiedAt(now);
        repository.save(record);
    }

    @Transactional(readOnly = true)
    public boolean isVerified(String rawEmail, String purpose) {
        String email = normalizeEmail(rawEmail);
        String normalizedPurpose = normalizePurpose(purpose);

        return repository.findTopByEmailAndPurposeOrderByCreatedAtDesc(email, normalizedPurpose)
                .map(record -> record.getVerifiedAt() != null
                        && record.getExpiresAt().isAfter(LocalDateTime.now(ZoneOffset.UTC)))
                .orElse(false);
    }

    @Transactional
    public void consumeVerification(String rawEmail, String purpose) {
        repository.deleteByEmailAndPurpose(normalizeEmail(rawEmail), normalizePurpose(purpose));
    }

    private void sendEmail(String email, String otp, String purpose) {
        String action = "REGISTER".equals(purpose) ? "create your EduHub account" : "sign in to EduHub";
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your EduHub verification code");
        message.setText("""
                Your EduHub verification code is: %s

                Use this code to %s.
                This code expires in %d minutes.
                If you did not request this code, you can safely ignore this email.
                """.formatted(otp, action, OTP_EXPIRY_MINUTES));
        mailSender.send(message);
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private static String normalizePurpose(String purpose) {
        return purpose.trim().toUpperCase(Locale.ROOT);
    }

    private static String sha256(String value) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256")
                            .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Unable to hash OTP", e);
        }
    }
}
