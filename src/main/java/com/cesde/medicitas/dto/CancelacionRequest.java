package com.cesde.medicitas.dto;

import jakarta.validation.constraints.NotBlank;

public record CancelacionRequest(
        @NotBlank String motivoCancelacion
) {}
