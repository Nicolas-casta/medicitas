package com.cesde.medicitas.service;

import com.cesde.medicitas.dto.LoginRequest;
import com.cesde.medicitas.dto.AuthResponse;
import com.cesde.medicitas.entity.User;
import com.cesde.medicitas.enums.Role;
import com.cesde.medicitas.exception.InvalidCredentialsException;
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
@DisplayName("AuthService - US-002: Login")
class AuthServiceLoginTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private UserMapper userMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    private User mockUser;
    private LoginRequest validLogin;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .email("juan.perez@email.com")
                .password("$2a$10$hashedPassword")
                .role(Role.PATIENT)
                .build();

        validLogin = new LoginRequest("juan.perez@email.com", "password123");
    }

    @Test
    @DisplayName("Should login successfully and return tokens")
    void login_WithValidCredentials_ShouldReturnAuthResponse() {
        when(userRepository.findByEmail("juan.perez@email.com")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password123", "$2a$10$hashedPassword")).thenReturn(true);
        when(jwtService.generateAccessToken(any())).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh-token");

        AuthResponse response = authService.login(validLogin);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        verify(jwtService).generateAccessToken(mockUser);
        verify(jwtService).generateRefreshToken(mockUser);
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when email not found")
    void login_WithUnknownEmail_ShouldThrowException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.login(validLogin));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when password is wrong")
    void login_WithWrongPassword_ShouldThrowException() {
        when(userRepository.findByEmail("juan.perez@email.com")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(validLogin));
        verify(jwtService, never()).generateAccessToken(any());
    }
}