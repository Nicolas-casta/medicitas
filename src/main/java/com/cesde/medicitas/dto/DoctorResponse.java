package com.cesde.medicitas.dto;

public record DoctorResponse(
        Long id, String nombreCompleto, String email,
        String telefono, String especialidad,
        String licenciaMedica, boolean activo
) {}