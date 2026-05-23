package com.cesde.medicitas.service.impl;

import com.cesde.medicitas.dto.ReportePeriodoResponse;
import com.cesde.medicitas.dto.ReporteProductividadResponse;
import com.cesde.medicitas.entity.Cita;
import com.cesde.medicitas.entity.Doctor;
import com.cesde.medicitas.enums.EstadoCita;
import com.cesde.medicitas.repository.CitaRepository;
import com.cesde.medicitas.repository.DoctorRepository;
import com.cesde.medicitas.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final CitaRepository citaRepository;
    private final DoctorRepository doctorRepository;

    // ─── US-018 ───────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public ReportePeriodoResponse reportePorPeriodo(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Cita> citas = citaRepository.findByFechaBetween(fechaInicio, fechaFin);

        long agendadas  = citas.stream().filter(c -> c.getEstado() == EstadoCita.AGENDADA).count();
        long confirmadas = citas.stream().filter(c -> c.getEstado() == EstadoCita.CONFIRMADA).count();
        long atendidas  = citas.stream().filter(c -> c.getEstado() == EstadoCita.ATENDIDA).count();
        long canceladas = citas.stream().filter(c -> c.getEstado() == EstadoCita.CANCELADA).count();
        long noAsistio  = citas.stream().filter(c -> c.getEstado() == EstadoCita.NO_ASISTIO).count();

        Map<String, Long> porEspecialidad = citas.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getDoctor().getSpecialty().getNombre(),
                        Collectors.counting()
                ));

        return new ReportePeriodoResponse(
                citas.size(), agendadas + confirmadas, atendidas, canceladas, noAsistio, porEspecialidad
        );
    }

    // ─── US-019 ───────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<ReporteProductividadResponse> reporteProductividad(LocalDate fechaInicio, LocalDate fechaFin, Long doctorId) {
        List<Doctor> doctores = (doctorId != null)
                ? doctorRepository.findById(doctorId).map(List::of).orElse(List.of())
                : doctorRepository.findAll();

        return doctores.stream().map(doctor -> {
            List<Cita> citas = citaRepository.findByDoctorIdAndFechaBetween(doctor.getId(), fechaInicio, fechaFin);

            long atendidas  = citas.stream().filter(c -> c.getEstado() == EstadoCita.ATENDIDA).count();
            long canceladas = citas.stream().filter(c -> c.getEstado() == EstadoCita.CANCELADA).count();

            double promedio = citas.stream()
                    .filter(c -> c.getHoraInicioAtencion() != null && c.getHoraFinAtencion() != null)
                    .mapToLong(c -> Duration.between(c.getHoraInicioAtencion(), c.getHoraFinAtencion()).toMinutes())
                    .average()
                    .orElse(0.0);

            return new ReporteProductividadResponse(
                    doctor.getId(),
                    doctor.getUser().getNombre() + " " + doctor.getUser().getApellido(),
                    doctor.getSpecialty().getNombre(),
                    atendidas, canceladas, promedio
            );
        }).toList();
    }
}
