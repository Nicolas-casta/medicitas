package com.cesde.medicitas.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record ScheduleResponse(
        Long id, DayOfWeek diaSemana,
        LocalTime horaInicio, LocalTime horaFin,
        Integer duracionCitaMinutos
) {}
