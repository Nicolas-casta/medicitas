package com.cesde.medicitas.dto;

import com.cesde.medicitas.enums.TipoNotificacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record NotificacionRequest(
        @NotBlank String destinatario,
        @NotNull TipoNotificacion tipo,
        @NotBlank String mensaje,
        Map<String, Object> metadata
) {}
