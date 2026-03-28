package com.cesde.medicitas.controller;

import com.cesde.medicitas.dto.DoctorRequest;
import com.cesde.medicitas.dto.DoctorResponse;
import com.cesde.medicitas.dto.DoctorUpdateRequest;
import com.cesde.medicitas.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctors", description = "US-005, US-006")
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "US-005: Crear doctor")
    public ResponseEntity<DoctorResponse> create(@Valid @RequestBody DoctorRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.create(req));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA', 'PACIENTE')")
    @Operation(summary = "US-005/006: Listar doctores (filtrar por especialidad)")
    public ResponseEntity<List<DoctorResponse>> findAll(
            @RequestParam(required = false) Long specialtyId) {
        if (specialtyId != null) return ResponseEntity.ok(doctorService.findBySpecialty(specialtyId));
        return ResponseEntity.ok(doctorService.findAll());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "US-005: Actualizar doctor")
    public ResponseEntity<DoctorResponse> update(@PathVariable Long id,
                                                 @RequestBody DoctorUpdateRequest req) {
        return ResponseEntity.ok(doctorService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "US-005: Desactivar doctor")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        doctorService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
