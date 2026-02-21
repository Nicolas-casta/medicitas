package com.cesde.medicitas.service;

import com.cesde.medicitas.model.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {
    private static final String SECRET = "tu_clave_secreta_super_larga_de_64_caracteres_minimo_123456";

    public String generateToken(Usuario usuario) {
        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("userId", usuario.getId())
                .claim("rol", usuario.getRol())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .compact();
    }

    public String generateRefreshToken(Usuario usuario) {
        return Jwts.builder()
                .subject(usuario.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 604800000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .compact();
    }

    public boolean esTokenValido(String token) {
        try {
            Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false; // Expirado o alterado
        }
    }

    public String extraerEmail(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
