package com.cesde.medicitas.config;

import com.cesde.medicitas.entity.Patient;
import com.cesde.medicitas.entity.User;
import com.cesde.medicitas.enums.Role;
import com.cesde.medicitas.repository.PatientRepository;
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
                                      PatientRepository patientRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            // ADMINISTRADOR
            if (!userRepository.existsByEmail("admin@medicitas.com")
                    && !userRepository.existsByDocumento("000000000")) {
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

            // RECEPCIONISTA
            if (!userRepository.existsByEmail("recep@medicitas.com")
                    && !userRepository.existsByDocumento("000000002")) {
                userRepository.save(User.builder()
                        .nombre("Laura").apellido("Gómez")
                        .documento("000000002").email("recep@medicitas.com")
                        .telefono("3000000002").fechaNacimiento(LocalDate.of(1995, 3, 15))
                        .password(passwordEncoder.encode("recep1234"))
                        .role(Role.RECEPCIONISTA).activo(true).build());
                System.out.println("RECEPCIONISTA creado: recep@medicitas.com / recep1234");
            }

            // PACIENTE
            if (!userRepository.existsByEmail("paciente@medicitas.com")
                    && !userRepository.existsByDocumento("000000003")) {
                User pacienteUser = User.builder()
                        .nombre("Juan")
                        .apellido("Pérez")
                        .documento("000000003")
                        .email("paciente@medicitas.com")
                        .telefono("3000000003")
                        .fechaNacimiento(LocalDate.of(2000, 6, 20))
                        .password(passwordEncoder.encode("paciente1234"))
                        .role(Role.PACIENTE)
                        .activo(true)
                        .build();
                userRepository.save(pacienteUser);
                System.out.println("PACIENTE creado: paciente@medicitas.com / paciente1234");

                Patient patient = Patient.builder()
                        .user(pacienteUser)
                        .direccion("Calle 123")
                        .eps("Sura")
                        .tipoSangre("O+")
                        .build();
                patientRepository.save(patient);
                System.out.println("Registro de paciente creado");
            }
        };
    }
}