package com.cesde.medicitas.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record ConfirmacionRequest(
        @NotNull LocalTime horaLlegada
) {}
