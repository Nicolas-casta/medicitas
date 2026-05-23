package com.cesde.medicitas.service.impl;

import com.cesde.medicitas.dto.NotificacionRequest;
import com.cesde.medicitas.dto.NotificacionResponse;
import com.cesde.medicitas.service.NotificacionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class NotificacionServiceImpl implements NotificacionService {

    // Contador simple de ID en memoria (simula el microservicio)
    private final AtomicLong counter = new AtomicLong(1);

    @Override
    public NotificacionResponse enviar(NotificacionRequest request) {
        Long id = counter.getAndIncrement();
        LocalDateTime ahora = LocalDateTime.now();

        // Simular envío con log con formato específico (criterio US-020)
        log.info("[NOTIFICATION-SERVICE] id={} | tipo={} | destinatario={} | mensaje=\"{}\" | metadata={} | timestamp={}",
                id,
                request.tipo(),
                request.destinatario(),
                request.mensaje(),
                request.metadata(),
                ahora);

        return new NotificacionResponse(id, request.destinatario(), request.tipo(),
                request.mensaje(), true, ahora);
    }
}
