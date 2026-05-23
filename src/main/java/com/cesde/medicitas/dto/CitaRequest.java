package com.cesde.medicitas.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record CitaRequest(
        @NotNull Long patientId,
        @NotNull Long doctorId,
        @NotNull @FutureOrPresent LocalDate fecha,
        @NotNull LocalTime horaInicio,
        @NotBlank String motivoConsulta
) {}