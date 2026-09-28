package com.edunest.backend.modules.auth.controller;

import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.auth.dto.request.LoginRequest;
import com.edunest.backend.modules.auth.dto.request.OtpRequest;
import com.edunest.backend.modules.auth.dto.request.RefreshTokenRequest;
import com.edunest.backend.modules.auth.dto.request.RegisterRequest;
import com.edunest.backend.modules.auth.dto.response.AuthResponse;
import com.edunest.backend.modules.auth.dto.response.CurrentUserResponse;
import com.edunest.backend.modules.auth.service.AuthService;
import com.edunest.backend.modules.auth.service.EmailOtpService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final EmailOtpService emailOtpService;

    public AuthController(AuthService authService, EmailOtpService emailOtpService) {
        this.authService = authService;
        this.emailOtpService = emailOtpService;
    }

    @PostMapping("/otp/send")
    public ResponseEntity<ApiResponse<Void>> sendOtp(@Valid @RequestBody OtpRequest request) {
        emailOtpService.sendOtp(request.getEmail(), request.getPurpose());
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("OTP sent successfully")
                .build());
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(@Valid @RequestBody OtpRequest request) {
        if (request.getOtp() == null || request.getOtp().isBlank()) {
            throw new com.edunest.backend.common.exception.BadRequestException("OTP is required");
        }
        emailOtpService.verifyOtp(request.getEmail(), request.getPurpose(), request.getOtp());
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("OTP verified successfully")
                .build());
    }

    /**
     * Convenience endpoint for Swagger and API clients.
     * The frontend can continue using /auth/otp/send with purpose=REGISTER.
     */
    @PostMapping("/register/send-otp")
    public ResponseEntity<ApiResponse<Void>> sendRegisterOtp(@Valid @RequestBody OtpRequest request) {
        requirePurpose(request, "REGISTER");
        emailOtpService.sendOtp(request.getEmail(), "REGISTER");
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Registration OTP sent successfully")
                .build());
    }

    /**
     * Convenience endpoint for Swagger and API clients.
     * The frontend can continue using /auth/otp/send with purpose=LOGIN.
     */
    @PostMapping("/login/send-otp")
    public ResponseEntity<ApiResponse<Void>> sendLoginOtp(@Valid @RequestBody OtpRequest request) {
        requirePurpose(request, "LOGIN");
        emailOtpService.sendOtp(request.getEmail(), "LOGIN");
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Login OTP sent successfully")
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        AuthResponse response = authService.register(request);

        ApiResponse<AuthResponse> apiResponse = ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("User registered successfully")
                .data(response)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);

        ApiResponse<AuthResponse> apiResponse = ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Login successful")
                .data(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @RequestBody RefreshTokenRequest request) {

        AuthResponse response = authService.refreshToken(request);

        ApiResponse<AuthResponse> apiResponse = ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Token refreshed successfully")
                .data(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @Valid @RequestBody RefreshTokenRequest request) {

        authService.logout(request);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Logout successful")
                .data("Token removed")
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CurrentUserResponse>> getCurrentUser() {

        CurrentUserResponse responseData = authService.getCurrentUser();

        ApiResponse<CurrentUserResponse> response = ApiResponse.<CurrentUserResponse>builder()
                .success(true)
                .message("Current user fetched successfully")
                .data(responseData)
                .build();

        return ResponseEntity.ok(response);
    }

    private void requirePurpose(OtpRequest request, String expectedPurpose) {
        if (!expectedPurpose.equalsIgnoreCase(request.getPurpose())) {
            throw new com.edunest.backend.common.exception.BadRequestException(
                    "Purpose must be " + expectedPurpose);
        }
    }
}
