package com.cesde.medicitas.dto;

public record ReporteProductividadResponse(
        Long doctorId,
        String nombreDoctor,
        String especialidad,
        long citasAtendidas,
        long citasCanceladas,
        double tiempoPromedioAtencionMinutos
) {}
