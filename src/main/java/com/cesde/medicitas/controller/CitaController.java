package com.cesde.medicitas.controller;

import com.cesde.medicitas.dto.*;
import com.cesde.medicitas.enums.EstadoCita;
import com.cesde.medicitas.security.JwtService;
import com.cesde.medicitas.service.CitaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/citas")
@RequiredArgsConstructor
@Tag(name = "Citas", description = "US-011 a US-017")
public class CitaController {

    private final CitaService citaService;
    private final JwtService jwtService;

    // ─── US-011: Recepcionista agenda cita ────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'RECEPCIONISTA')")
    @Operation(summary = "US-011: Agendar cita para un paciente")
    public ResponseEntity<CitaResponse> agendarCita(@Valid @RequestBody CitaRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(citaService.agendarCita(req));
    }

    // ─── US-012: Paciente solicita cita ───────────────────────────────────────

    @PostMapping("/solicitar")
    @PreAuthorize("hasAuthority('PACIENTE')")
    @Operation(summary = "US-012: Paciente solicita su propia cita")
    public ResponseEntity<CitaResponse> solicitarCita(
            @RequestHeader("Authorization") String auth,
            @Valid @RequestBody CitaRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(citaService.solicitarCita(req, extractUserId(auth)));
    }

    @GetMapping("/slots-disponibles")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'RECEPCIONISTA', 'PACIENTE')")
    @Operation(summary = "US-012: Ver slots disponibles de un doctor en una fecha")
    public ResponseEntity<SlotsDisponiblesResponse> getSlotsDisponibles(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(citaService.getSlotsDisponibles(doctorId, fecha));
    }

    // ─── US-013: Cancelar cita ────────────────────────────────────────────────

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'RECEPCIONISTA', 'PACIENTE')")
    @Operation(summary = "US-013: Cancelar una cita (paciente solo las suyas, RECEPCIONISTA/ADMIN cualquiera)")
    public ResponseEntity<CitaDetalleResponse> cancelar(
            @PathVariable Long id,
            @RequestHeader("Authorization") String auth,
            @Valid @RequestBody CancelacionRequest req) {
        Long userId = extractUserId(auth);
        String rol = extractRol(auth);
        return ResponseEntity.ok(citaService.cancelarCita(id, userId, rol, req));
    }

    // ─── US-014: Confirmar llegada ────────────────────────────────────────────

    @PatchMapping("/{id}/confirmar-llegada")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'RECEPCIONISTA')")
    @Operation(summary = "US-014: Confirmar que el paciente llegó a su cita")
    public ResponseEntity<CitaDetalleResponse> confirmarLlegada(
            @PathVariable Long id,
            @Valid @RequestBody ConfirmacionRequest req) {
        return ResponseEntity.ok(citaService.confirmarLlegada(id, req));
    }

    // ─── US-015: Atender cita ─────────────────────────────────────────────────

    @PatchMapping("/{id}/atender")
    @PreAuthorize("hasAuthority('DOCTOR')")
    @Operation(summary = "US-015: Doctor registra la atención de una cita")
    public ResponseEntity<CitaDetalleResponse> atenderCita(
            @PathVariable Long id,
            @RequestHeader("Authorization") String auth,
            @Valid @RequestBody AtencionRequest req) {
        return ResponseEntity.ok(citaService.atenderCita(id, extractUserId(auth), req));
    }

    // ─── US-016: Historial del paciente ──────────────────────────────────────

    @GetMapping("/mis-citas")
    @PreAuthorize("hasAuthority('PACIENTE')")
    @Operation(summary = "US-016: Paciente ve su historial de citas (filtros: estado, fechas)")
    public ResponseEntity<List<CitaDetalleResponse>> getMisCitas(
            @RequestHeader("Authorization") String auth,
            @RequestParam(required = false) EstadoCita estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(citaService.getMisCitas(extractUserId(auth), estado, desde, hasta));
    }

    // ─── US-017: Agenda del día del doctor ───────────────────────────────────

    @GetMapping("/mi-agenda-hoy")
    @PreAuthorize("hasAuthority('DOCTOR')")
    @Operation(summary = "US-017: Doctor ve su agenda de citas del día")
    public ResponseEntity<List<CitaDetalleResponse>> getAgendaHoy(
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(citaService.getAgendaHoy(extractUserId(auth)));
    }

    // ─── Consultas admin/recepcionista ────────────────────────────────────────

    @GetMapping("/paciente/{patientId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'RECEPCIONISTA')")
    @Operation(summary = "Listar citas de un paciente")
    public ResponseEntity<List<CitaResponse>> findByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(citaService.findByPatient(patientId));
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'RECEPCIONISTA', 'DOCTOR')")
    @Operation(summary = "Listar citas de un doctor en una fecha")
    public ResponseEntity<List<CitaResponse>> findByDoctorAndFecha(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(citaService.findByDoctorAndFecha(doctorId, fecha));
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private Long extractUserId(String auth) {
        return jwtService.extractUserId(auth.substring(7));
    }

    private String extractRol(String auth) {
        return jwtService.extractRol(auth.substring(7));
    }
}
