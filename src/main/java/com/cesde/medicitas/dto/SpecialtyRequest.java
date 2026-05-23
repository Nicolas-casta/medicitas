package com.cesde.medicitas.dto;

import jakarta.validation.constraints.NotBlank;

public record SpecialtyRequest(@NotBlank String nombre, String descripcion) {}
