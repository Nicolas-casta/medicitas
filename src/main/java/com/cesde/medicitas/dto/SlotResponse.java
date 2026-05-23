package com.cesde.medicitas.dto;

import java.time.LocalTime;

public record SlotResponse(LocalTime horaInicio, LocalTime horaFin, boolean disponible) {}
