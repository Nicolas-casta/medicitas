package com.cesde.medicitas.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record PatientRequest(
        @NotBlank String nombre,
        @NotBlank String apellido,
        @NotBlank String documento,
        @Email @NotBlank String email,
        @NotBlank String telefono,
        @NotNull LocalDate fechaNacimiento,
        String direccion,
        String eps,
        String tipoSangre
) {}