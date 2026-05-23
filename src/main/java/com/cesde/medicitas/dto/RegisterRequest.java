package com.cesde.medicitas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegisterRequest(
        @NotBlank String nombre,
        @NotBlank String apellido,
        @NotBlank String documento,
        @Email @NotBlank String email,
        @NotBlank String telefono,
        @NotNull LocalDate fechaNacimiento,
        @Size(min = 8) @NotBlank String password
) {}