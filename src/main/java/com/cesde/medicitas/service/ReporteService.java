package com.cesde.medicitas.service;

import com.cesde.medicitas.dto.ReportePeriodoResponse;
import com.cesde.medicitas.dto.ReporteProductividadResponse;

import java.time.LocalDate;
import java.util.List;

public interface ReporteService {

    // US-018
    ReportePeriodoResponse reportePorPeriodo(LocalDate fechaInicio, LocalDate fechaFin);

    // US-019
    List<ReporteProductividadResponse> reporteProductividad(LocalDate fechaInicio, LocalDate fechaFin, Long doctorId);
}
