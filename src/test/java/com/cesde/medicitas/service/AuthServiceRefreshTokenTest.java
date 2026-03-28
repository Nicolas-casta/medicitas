package com.cesde.medicitas.service;

import com.cesde.medicitas.dto.RefreshTokenRequest;
import com.cesde.medicitas.dto.RefreshTokenResponse;
import com.cesde.medicitas.entity.User;
import com.cesde.medicitas.enums.Role;
import com.cesde.medicitas.exception.InvalidTokenException;
import com.cesde.medicitas.mapper.UserMapper;
import com.cesde.medicitas.repository.UserRepository;
import com.cesde.medicitas.security.JwtService;
import com.cesde.medicitas.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - US-003: Refresh Token")
class AuthServiceRefreshTokenTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private UserMapper userMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    private User mockUser;
    private RefreshTokenRequest validRequest;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .email("juan.perez@email.com")
                .password("$2a$10$hashedPassword")
                .role(Role.PATIENT)
                .build();

        validRequest = new RefreshTokenRequest("valid-refresh-token");
    }

    @Test
    @DisplayName("Should return new access token when refresh token is valid")
    void refreshToken_WithValidToken_ShouldReturnNewAccessToken() {
        when(jwtService.extractUsername("valid-refresh-token")).thenReturn("juan.perez@email.com");
        when(userRepository.findByEmail("juan.perez@email.com")).thenReturn(Optional.of(mockUser));
        when(jwtService.isTokenValid("valid-refresh-token", mockUser)).thenReturn(true);
        when(jwtService.generateAccessToken(mockUser)).thenReturn("new-access-token");

        RefreshTokenResponse response = authService.refreshToken(validRequest);

        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());
        verify(jwtService).generateAccessToken(mockUser);
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when token is expired or invalid")
    void refreshToken_WithExpiredToken_ShouldThrowException() {
        when(jwtService.extractUsername("valid-refresh-token")).thenReturn("juan.perez@email.com");
        when(userRepository.findByEmail("juan.perez@email.com")).thenReturn(Optional.of(mockUser));
        when(jwtService.isTokenValid("valid-refresh-token", mockUser)).thenReturn(false);

        assertThrows(InvalidTokenException.class, () -> authService.refreshToken(validRequest));
        verify(jwtService, never()).generateAccessToken(any());
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when token is malformed")
    void refreshToken_WithMalformedToken_ShouldThrowException() {
        when(jwtService.extractUsername(anyString())).thenThrow(new RuntimeException("Malformed token"));

        assertThrows(InvalidTokenException.class, () -> authService.refreshToken(validRequest));
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when user no longer exists")
    void refreshToken_WithUnknownUser_ShouldThrowException() {
        when(jwtService.extractUsername("valid-refresh-token")).thenReturn("ghost@email.com");
        when(userRepository.findByEmail("ghost@email.com")).thenReturn(Optional.empty());

        assertThrows(InvalidTokenException.class, () -> authService.refreshToken(validRequest));
        verify(jwtService, never()).generateAccessToken(any());
    }
}