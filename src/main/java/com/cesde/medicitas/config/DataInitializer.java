package com.cesde.medicitas.config;

import com.cesde.medicitas.entity.User;
import com.cesde.medicitas.enums.Role;
import com.cesde.medicitas.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByEmail("admin@medicitas.com")) {
                User admin = User.builder()
                        .nombre("Admin")
                        .apellido("Sistema")
                        .documento("000000000")
                        .email("admin@medicitas.com")
                        .telefono("0000000000")
                        .fechaNacimiento(LocalDate.of(1990, 1, 1))
                        .password(passwordEncoder.encode("admin1234"))
                        .role(Role.ADMIN)
                        .activo(true)
                        .build();
                userRepository.save(admin);
                System.out.println("Usuario ADMIN creado: admin@medicitas.com / admin1234");
            }
        };
    }
}