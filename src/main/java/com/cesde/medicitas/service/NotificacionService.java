package com.cesde.medicitas.service;

import com.cesde.medicitas.dto.NotificacionRequest;
import com.cesde.medicitas.dto.NotificacionResponse;

public interface NotificacionService {
    // US-020: Enviar notificación (simula llamada al microservicio)
    NotificacionResponse enviar(NotificacionRequest request);
}
