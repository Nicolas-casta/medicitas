package com.cesde.medicitas.config;

import com.cesde.medicitas.entity.User;
import com.cesde.medicitas.enums.Role;
import com.cesde.medicitas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        createAdminIfNotExists();
    }

    private void createAdminIfNotExists() {
        if (userRepository.existsByEmail("admin@medicitas.com")) {
            log.info("Admin user already exists, skipping creation.");
            return;
        }

        User admin = User.builder()
                .firstName("Admin")
                .lastName("Medicitas")
                .identityDocument("00000000")
                .email("admin@medicitas.com")
                .phone("0000000000")
                .birthDate(LocalDate.of(1990, 1, 1))
                .password(passwordEncoder.encode("Admin1234!"))
                .role(Role.ADMIN)
                .build();

        userRepository.save(admin);
        log.info("Default admin created -> email: admin@medicitas.com | password: Admin1234!");
    }
}