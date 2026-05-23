package com.cesde.medicitas.controller;

import com.cesde.medicitas.dto.NotificacionRequest;
import com.cesde.medicitas.dto.NotificacionResponse;
import com.cesde.medicitas.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notificaciones", description = "US-020 - Microservicio de notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    // ─── US-020 ───────────────────────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'RECEPCIONISTA')")
    @Operation(summary = "US-020: Enviar notificación (simula microservicio externo)")
    public ResponseEntity<NotificacionResponse> enviar(@Valid @RequestBody NotificacionRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificacionService.enviar(req));
    }
}
