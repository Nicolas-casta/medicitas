package com.cesde.medicitas.dto;

public record TokenResponseDTO (
        String access_token,
        String refresh_token
){
}
