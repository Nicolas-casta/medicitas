package com.cesde.medicitas.dto;

import java.time.LocalTime;
import java.util.List;

public record SlotsDisponiblesResponse(
        Long doctorId,
        String nombreDoctor,
        String fecha,
        List<LocalTime> slotsDisponibles
) {}