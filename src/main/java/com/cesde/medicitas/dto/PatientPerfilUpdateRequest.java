package com.cesde.medicitas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PatientPerfilUpdateRequest(
        @Email
        @NotBlank
        String email,
        @NotBlank
        String telefono,
        String direccion
) {}