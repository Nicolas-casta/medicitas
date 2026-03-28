package com.cesde.medicitas.service.ipml;

import com.cesde.medicitas.dto.*;
import com.cesde.medicitas.entity.User;
import com.cesde.medicitas.enums.Role;
import com.cesde.medicitas.exception.EmailAlreadyExistsException;
import com.cesde.medicitas.exception.InvalidCredentialsException;
import com.cesde.medicitas.exception.InvalidTokenException;
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

    @Override
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email()))
            throw new EmailAlreadyExistsException("Email ya registrado: " + req.email());

        User user = User.builder()
                .nombre(req.nombre()).apellido(req.apellido())
                .documento(req.documento()).email(req.email())
                .telefono(req.telefono()).fechaNacimiento(req.fechaNacimiento())
                .password(passwordEncoder.encode(req.password()))
                .role(Role.PACIENTE).activo(true)
                .build();

        userRepository.save(user);
        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new InvalidCredentialsException("Credenciales inválidas"));
        if (!passwordEncoder.matches(req.password(), user.getPassword()))
            throw new InvalidCredentialsException("Credenciales inválidas");
        return buildAuthResponse(user);
    }

    @Override
    public RefreshTokenResponse refresh(RefreshTokenRequest req) {
        if (!jwtService.isTokenValid(req.refreshToken()))
            throw new InvalidTokenException("Refresh token inválido o expirado");
        String email = jwtService.extractEmail(req.refreshToken());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidTokenException("Usuario no encontrado"));
        return new RefreshTokenResponse(
                jwtService.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name())
        );
    }

    private AuthResponse buildAuthResponse(User user) {
        String access = jwtService.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());
        String refresh = jwtService.generateRefreshToken(user.getEmail());
        return new AuthResponse(access, refresh);
    }
}
