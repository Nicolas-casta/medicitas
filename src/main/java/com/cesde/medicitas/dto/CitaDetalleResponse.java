package com.cesde.medicitas.dto;

import com.cesde.medicitas.enums.EstadoCita;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record CitaDetalleResponse(
        Long id,
        Long patientId,
        String nombrePaciente,
        String documentoPaciente,
        Long doctorId,
        String nombreDoctor,
        String especialidad,
        LocalDate fecha,
        LocalTime horaInicio,
        String motivoConsulta,
        EstadoCita estado,
        // cancelación
        String motivoCancelacion,
        LocalDateTime fechaCancelacion,
        // confirmación
        LocalTime horaLlegada,
        // atención
        String diagnostico,
        String observaciones,
        String indicaciones,
        LocalTime horaInicioAtencion,
        LocalTime horaFinAtencion
) {}
