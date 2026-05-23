package com.cesde.medicitas.service;

import com.cesde.medicitas.dto.*;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    RefreshTokenResponse refresh(RefreshTokenRequest request);
}