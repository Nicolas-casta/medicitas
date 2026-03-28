package com.cesde.medicitas.controller;

import com.cesde.medicitas.dto.ScheduleRequest;
import com.cesde.medicitas.dto.ScheduleResponse;
import com.cesde.medicitas.dto.SlotResponse;
import com.cesde.medicitas.service.ScheduleService;
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
@RequestMapping("/api/v1/doctors/{doctorId}/schedules")
@RequiredArgsConstructor
@Tag(name = "Schedules", description = "US-007, US-008")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "US-007: Configurar horario del doctor")
    public ResponseEntity<ScheduleResponse> create(@PathVariable Long doctorId,
                                                   @Valid @RequestBody ScheduleRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(scheduleService.createSchedule(doctorId, req));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "US-007: Ver horarios del doctor")
    public ResponseEntity<List<ScheduleResponse>> getSchedules(@PathVariable Long doctorId) {
        return ResponseEntity.ok(scheduleService.getSchedulesByDoctor(doctorId));
    }

    @GetMapping("/slots")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    @Operation(summary = "US-008: Ver slots disponibles")
    public ResponseEntity<List<SlotResponse>> getSlots(@PathVariable Long doctorId,
                                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(scheduleService.getAvailableSlots(doctorId, date));
    }
}
