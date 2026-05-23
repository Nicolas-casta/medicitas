package com.cesde.medicitas.dto;

import com.cesde.medicitas.enums.TipoNotificacion;
import java.time.LocalDateTime;

public record NotificacionResponse(
        Long id,
        String destinatario,
        TipoNotificacion tipo,
        String mensaje,
        boolean enviada,
        LocalDateTime fechaEnvio
) {}
