package com.cesde.medicitas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record DoctorRequest(
        @NotBlank String nombre,
        @NotBlank String apellido,
        @NotBlank String documento,
        @Email @NotBlank String email,
        @NotBlank String telefono,
        @NotNull LocalDate fechaNacimiento,
        @NotNull Long specialtyId,
        @NotBlank String licenciaMedica
) {}