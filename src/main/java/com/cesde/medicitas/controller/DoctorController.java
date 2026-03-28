package com.cesde.medicitas.controller;

import com.cesde.medicitas.dto.DoctorRequest;
import com.cesde.medicitas.dto.DoctorResponse;
import com.cesde.medicitas.dto.DoctorUpdateRequest;
import com.cesde.medicitas.exception.ErrorResponse;
import com.cesde.medicitas.service.DoctorService;
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
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctors", description = "CRUD de doctores - Solo ADMIN")
@SecurityRequirement(name = "bearerAuth")
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create doctor", description = "Creates a doctor and its associated DOCTOR user account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Doctor created",
                    content = @Content(schema = @Schema(implementation = DoctorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Email or license already registered",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<DoctorResponse> create(@Valid @RequestBody DoctorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.create(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List doctors", description = "Lists all active doctors. Optionally filter by specialtyId.")
    @ApiResponse(responseCode = "200", description = "Doctors listed successfully")
    public ResponseEntity<List<DoctorResponse>> findAll(
            @RequestParam(required = false) Long specialtyId) {
        return ResponseEntity.ok(doctorService.findAll(specialtyId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update doctor", description = "Updates an active doctor's data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor updated",
                    content = @Content(schema = @Schema(implementation = DoctorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Doctor not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "License number already registered",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<DoctorResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody DoctorUpdateRequest request) {
        return ResponseEntity.ok(doctorService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate doctor", description = "Soft deletes a doctor and disables their user account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Doctor deactivated"),
            @ApiResponse(responseCode = "404", description = "Doctor not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        doctorService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}