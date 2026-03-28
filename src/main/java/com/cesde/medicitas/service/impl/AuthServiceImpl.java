package com.cesde.medicitas.service.impl;

import com.cesde.medicitas.dto.LoginRequest;
import com.cesde.medicitas.dto.RefreshTokenRequest;
import com.cesde.medicitas.dto.RegisterRequest;
import com.cesde.medicitas.dto.AuthResponse;
import com.cesde.medicitas.dto.RefreshTokenResponse;
import com.cesde.medicitas.entity.User;
import com.cesde.medicitas.exception.EmailAlreadyExistsException;
import com.cesde.medicitas.exception.InvalidCredentialsException;
import com.cesde.medicitas.exception.InvalidTokenException;
import com.cesde.medicitas.mapper.UserMapper;
import com.cesde.medicitas.repository.UserRepository;
import com.cesde.medicitas.security.JwtService;
import com.cesde.medicitas.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User user = userMapper.toEntity(request, hashedPassword);
        User savedUser = userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        final String token = request.getRefreshToken();
        final String email;

        try {
            email = jwtService.extractUsername(token);
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (!jwtService.isTokenValid(token, user)) {
            throw new InvalidTokenException("Refresh token has expired");
        }

        String newAccessToken = jwtService.generateAccessToken(user);

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .build();
    }
}