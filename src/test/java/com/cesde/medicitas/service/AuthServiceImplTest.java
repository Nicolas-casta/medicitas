package com.cesde.medicitas.service;

import com.cesde.medicitas.dto.RegisterRequest;
import com.cesde.medicitas.dto.AuthResponse;
import com.cesde.medicitas.entity.User;
import com.cesde.medicitas.exception.EmailAlreadyExistsException;
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

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - US-001: Patient Registration")
class AuthServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private UserMapper userMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest validRequest;
    private User mockUser;

    @BeforeEach
    void setUp() {
        validRequest = RegisterRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .identityDocument("12345678")
                .email("john.doe@email.com")
                .phone("3001234567")
                .birthDate(LocalDate.of(1990, 5, 15))
                .password("password123")
                .build();

        mockUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@email.com")
                .build();
    }

    @Test
    @DisplayName("Should register patient successfully and return tokens")
    void register_WithValidData_ShouldReturnAuthResponse() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userMapper.toEntity(any(), anyString())).thenReturn(mockUser);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(jwtService.generateAccessToken(any())).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh-token");
        when(userMapper.toResponse(any())).thenReturn(null);

        // Act
        AuthResponse response = authService.register(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());

        verify(userRepository).existsByEmail("john.doe@email.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException when email is already registered")
    void register_WithDuplicateEmail_ShouldThrowException() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> authService.register(validRequest)
        );

        assertTrue(exception.getMessage().contains("john.doe@email.com"));
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Should encode password with BCrypt before saving")
    void register_ShouldEncodePassword_BeforeSaving() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashedPassword");
        when(userMapper.toEntity(any(), eq("$2a$10$hashedPassword"))).thenReturn(mockUser);
        when(userRepository.save(any())).thenReturn(mockUser);
        when(jwtService.generateAccessToken(any())).thenReturn("token");
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh");
        when(userMapper.toResponse(any())).thenReturn(null);

        // Act
        authService.register(validRequest);

        // Assert
        verify(passwordEncoder).encode("password123");
        verify(userMapper).toEntity(validRequest, "$2a$10$hashedPassword");
    }
}
