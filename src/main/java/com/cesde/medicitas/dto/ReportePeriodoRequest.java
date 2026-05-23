package com.cesde.medicitas.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ReportePeriodoRequest(
        @NotNull LocalDate fechaInicio,
        @NotNull LocalDate fechaFin
) {}
