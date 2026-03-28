package com.cesde.medicitas.controller;

import com.cesde.medicitas.dto.SpecialtyRequest;
import com.cesde.medicitas.dto.SpecialtyResponse;
import com.cesde.medicitas.exception.ErrorResponse;
import com.cesde.medicitas.service.SpecialtyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@Tag(name = "Specialties", description = "CRUD de especialidades médicas - Solo ADMIN")
@SecurityRequirement(name = "bearerAuth")
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create specialty", description = "Creates a new medical specialty. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Specialty created",
                    content = @Content(schema = @Schema(implementation = SpecialtyResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Specialty name already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<SpecialtyResponse> create(@Valid @RequestBody SpecialtyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(specialtyService.create(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List active specialties", description = "Returns all active specialties. Requires ADMIN role.")
    @ApiResponse(responseCode = "200", description = "Specialties listed successfully")
    public ResponseEntity<List<SpecialtyResponse>> findAllActive() {
        return ResponseEntity.ok(specialtyService.findAllActive());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update specialty", description = "Updates an existing active specialty. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Specialty updated",
                    content = @Content(schema = @Schema(implementation = SpecialtyResponse.class))),
            @ApiResponse(responseCode = "404", description = "Specialty not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Specialty name already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<SpecialtyResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody SpecialtyRequest request) {
        return ResponseEntity.ok(specialtyService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate specialty", description = "Soft deletes a specialty (logical delete). Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Specialty deactivated"),
            @ApiResponse(responseCode = "404", description = "Specialty not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        specialtyService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}