package com.edunest.backend.modules.auth.service.impl;

import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.modules.auth.dto.request.LoginRequest;
import com.edunest.backend.modules.auth.dto.request.RefreshTokenRequest;
import com.edunest.backend.modules.auth.dto.request.RegisterRequest;
import com.edunest.backend.modules.auth.dto.response.AuthResponse;
import com.edunest.backend.modules.auth.dto.response.CurrentUserResponse;
import com.edunest.backend.modules.auth.entity.RefreshToken;
import com.edunest.backend.modules.auth.repository.RefreshTokenRepository;
import com.edunest.backend.modules.auth.service.AuthService;
import com.edunest.backend.modules.role.entity.Role;
import com.edunest.backend.modules.role.repository.RoleRepository;
import com.edunest.backend.modules.user.entity.User;
import com.edunest.backend.modules.user.repository.UserRepository;
import com.edunest.backend.modules.userprofile.repository.UserAcademicProfileRepository;
import com.edunest.backend.common.enums.RoleType;
import com.edunest.backend.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserAcademicProfileRepository profileRepository;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.getEmail());
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email is already registered");
        }

        Role userRole = roleRepository.findByName(RoleType.USER)
                .orElseThrow(() -> new IllegalStateException("USER role is not configured"));

        User user = User.builder()
                .fullName(request.getFullName().trim())
                .email(email)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .role(userRole)
                .build();

        User saved = userRepository.save(user);
        return issueTokens(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CurrentUserResponse getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));

        return CurrentUserResponse.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().getName().name())
                .profileCompleted(profileRepository.findByUserId(user.getId()).isPresent())
                .build();
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.getEmail());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword()));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        // Rotate all previous refresh tokens on login to reduce replay risk.
        refreshTokenRepository.deleteByUser(user);
        return issueTokens(user);
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String rawToken = request.getRefreshToken();
        String hash = sha256(rawToken);

        RefreshToken stored = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

        if (stored.getExpiryDate().isBefore(LocalDateTime.now(java.time.ZoneOffset.UTC))
                || !jwtService.isRefreshTokenValid(rawToken, stored.getUser().getEmail())) {
            refreshTokenRepository.delete(stored);
            throw new BadRequestException("Invalid or expired refresh token");
        }

        User user = stored.getUser();

        // Rotate refresh token: one-time use.
        refreshTokenRepository.delete(stored);
        return issueTokens(user);
    }

    @Override
    @Transactional
    public void logout(RefreshTokenRequest request) {
        refreshTokenRepository.deleteByTokenHash(sha256(request.getRefreshToken()));
    }

    private AuthResponse issueTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        refreshTokenRepository.save(RefreshToken.builder()
                .tokenHash(sha256(refreshToken))
                .user(user)
                .expiryDate(jwtService.getRefreshTokenExpiryDate())
                .build());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(900L)
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().getName().name())
                .build();
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase(java.util.Locale.ROOT);
    }

    private static String sha256(String value) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256")
                            .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Unable to hash refresh token", e);
        }
    }
}
