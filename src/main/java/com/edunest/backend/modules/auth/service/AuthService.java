package com.edunest.backend.modules.auth.service;

import com.edunest.backend.modules.auth.dto.request.LoginRequest;
import com.edunest.backend.modules.auth.dto.request.RefreshTokenRequest;
import com.edunest.backend.modules.auth.dto.request.RegisterRequest;
import com.edunest.backend.modules.auth.dto.response.AuthResponse;
import com.edunest.backend.modules.auth.dto.response.CurrentUserResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);
    
    void logout(RefreshTokenRequest request);
    
    CurrentUserResponse getCurrentUser();
}