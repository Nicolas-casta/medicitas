package com.cesde.medicitas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record AtencionRequest(
        @NotBlank String diagnostico,
        String observaciones,
        String indicaciones,
        @NotNull LocalTime horaInicioAtencion,
        @NotNull LocalTime horaFinAtencion
) {}
