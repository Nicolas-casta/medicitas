package com.cesde.medicitas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UsuarioDTO (
        @NotBlank String nombre,
        @NotBlank String apellido,
        @NotBlank String documentoIdentidad,
        @Email @NotBlank String email,
        String telefono,
        @NotNull LocalDate fechaNacimiento,
        @Size(min = 8) String password
) {}