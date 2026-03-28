package com.cesde.medicitas.service;

import com.cesde.medicitas.dto.LoginRequest;
import com.cesde.medicitas.dto.RefreshTokenRequest;
import com.cesde.medicitas.dto.RegisterRequest;
import com.cesde.medicitas.dto.AuthResponse;
import com.cesde.medicitas.dto.RefreshTokenResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    RefreshTokenResponse refreshToken(RefreshTokenRequest request);
}