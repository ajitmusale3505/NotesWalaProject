package com.edunest.backend.modules.auth.controller;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.auth.dto.request.LoginRequest;
import com.edunest.backend.modules.auth.dto.request.RefreshTokenRequest;
import com.edunest.backend.modules.auth.dto.request.RegisterRequest;
import com.edunest.backend.modules.auth.dto.response.AuthResponse;
import com.edunest.backend.modules.auth.service.AuthService;

import com.edunest.backend.modules.auth.dto.response.CurrentUserResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        AuthResponse response = authService.register(request);

        ApiResponse<AuthResponse> apiResponse =
                ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .message("User registered successfully")
                        .data(response)
                        .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(apiResponse);
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);

        ApiResponse<AuthResponse> apiResponse =
                ApiResponse.<AuthResponse>builder()
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

        ApiResponse<AuthResponse> apiResponse =
                ApiResponse.<AuthResponse>builder()
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

        ApiResponse<String> response =
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Logout successful")
                        .data("Token removed")
                        .build();

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CurrentUserResponse>> getCurrentUser() {

        CurrentUserResponse responseData =
                authService.getCurrentUser();

        ApiResponse<CurrentUserResponse> response =
                ApiResponse.<CurrentUserResponse>builder()
                        .success(true)
                        .message("Current user fetched successfully")
                        .data(responseData)
                        .build();

        return ResponseEntity.ok(response);
    }
}