package com.cesde.medicitas.dto;

import com.cesde.medicitas.enums.EstadoCita;

import java.time.LocalDate;
import java.time.LocalTime;

public record CitaResponse(
        Long id,
        Long patientId,
        String nombrePaciente,
        Long doctorId,
        String nombreDoctor,
        String especialidad,
        LocalDate fecha,
        LocalTime horaInicio,
        String motivoConsulta,
        EstadoCita estado
) {}