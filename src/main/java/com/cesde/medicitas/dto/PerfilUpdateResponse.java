package com.cesde.medicitas.dto;

public record PerfilUpdateResponse(
        PatientResponse patient,
        String accessToken,
        String refreshToken
) {}
