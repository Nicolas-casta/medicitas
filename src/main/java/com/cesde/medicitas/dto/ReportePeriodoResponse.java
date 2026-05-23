package com.cesde.medicitas.dto;

import java.util.Map;

public record ReportePeriodoResponse(
        long totalCitas,
        long agendadas,
        long atendidas,
        long canceladas,
        long noAsistio,
        Map<String, Long> porEspecialidad
) {}
