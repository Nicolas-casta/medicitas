package com.cesde.medicitas.controller;

import com.cesde.medicitas.dto.SpecialtyRequest;
import com.cesde.medicitas.dto.SpecialtyResponse;
import com.cesde.medicitas.service.SpecialtyService;
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
@RequestMapping("/api/v1/specialties")
@RequiredArgsConstructor
@Tag(name = "Specialties", description = "US-004")
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "US-004: Crear especialidad")
    public ResponseEntity<SpecialtyResponse> create(@Valid @RequestBody SpecialtyRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(specialtyService.create(req));
    }

    @GetMapping
    @Operation(summary = "US-004: Listar especialidades activas")
    public ResponseEntity<List<SpecialtyResponse>> findAll() {
        return ResponseEntity.ok(specialtyService.findAllActive());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "US-004: Actualizar especialidad")
    public ResponseEntity<SpecialtyResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody SpecialtyRequest req) {
        return ResponseEntity.ok(specialtyService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "US-004: Desactivar especialidad")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        specialtyService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}