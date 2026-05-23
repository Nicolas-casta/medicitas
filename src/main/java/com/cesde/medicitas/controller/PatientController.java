package com.cesde.medicitas.controller;

import com.cesde.medicitas.dto.PatientPerfilUpdateRequest;
import com.cesde.medicitas.dto.PatientRequest;
import com.cesde.medicitas.dto.PatientResponse;
import com.cesde.medicitas.dto.PerfilUpdateResponse;
import com.cesde.medicitas.security.JwtService;
import com.cesde.medicitas.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
@Tag(name = "Patients", description = "US-009, US-010")
public class PatientController {

    private final PatientService patientService;
    private final JwtService jwtService;

    // ─── US-009 ───────────────────────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'RECEPCIONISTA')")
    @Operation(summary = "US-009: Crear paciente")
    public ResponseEntity<PatientResponse> create(@Valid @RequestBody PatientRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.create(req));
    }

    @GetMapping("/documento/{documento}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'RECEPCIONISTA')")
    @Operation(summary = "US-009: Buscar paciente por documento")
    public ResponseEntity<PatientResponse> findByDocumento(@PathVariable String documento) {
        return ResponseEntity.ok(patientService.findByDocumento(documento));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'RECEPCIONISTA')")
    @Operation(summary = "US-009: Listar pacientes paginado, filtro por nombre")
    public ResponseEntity<Page<PatientResponse>> findAll(
            @RequestParam(required = false) String nombre,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(patientService.findAll(nombre, pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'RECEPCIONISTA')")
    @Operation(summary = "US-009: Actualizar datos de paciente")
    public ResponseEntity<PatientResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody PatientRequest req) {
        return ResponseEntity.ok(patientService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "US-009: Eliminar paciente")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        patientService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ─── US-010 ───────────────────────────────────────────────────────────────

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('PACIENTE')")
    @Operation(summary = "US-010: Ver mi perfil")
    public ResponseEntity<PatientResponse> getMyProfile(
            @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(patientService.getMyProfile(extractUserId(authHeader)));
    }

    @PatchMapping("/me")
    @PreAuthorize("hasAuthority('PACIENTE')")
    @Operation(summary = "US-010: Actualizar mi perfil (email, teléfono, dirección)")
    public ResponseEntity<PerfilUpdateResponse> updateMyProfile(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody PatientPerfilUpdateRequest req) {
        return ResponseEntity.ok(patientService.updateMyProfile(extractUserId(authHeader), req));
    }

    private Long extractUserId(String authHeader) {
        return jwtService.extractUserId(authHeader.substring(7));
    }
}
