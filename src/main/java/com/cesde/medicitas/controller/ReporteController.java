package com.cesde.medicitas.controller;

import com.cesde.medicitas.dto.ReportePeriodoResponse;
import com.cesde.medicitas.dto.ReporteProductividadResponse;
import com.cesde.medicitas.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "US-018, US-019")
public class ReporteController {

    private final ReporteService reporteService;

    // ─── US-018 ───────────────────────────────────────────────────────────────

    @GetMapping("/periodo")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "US-018: Reporte de citas por período")
    public ResponseEntity<ReportePeriodoResponse> reportePorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ResponseEntity.ok(reporteService.reportePorPeriodo(fechaInicio, fechaFin));
    }

    // ─── US-019 ───────────────────────────────────────────────────────────────

    @GetMapping("/productividad")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "US-019: Reporte de productividad por doctor")
    public ResponseEntity<List<ReporteProductividadResponse>> reporteProductividad(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Long doctorId) {
        return ResponseEntity.ok(reporteService.reporteProductividad(fechaInicio, fechaFin, doctorId));
    }
}
