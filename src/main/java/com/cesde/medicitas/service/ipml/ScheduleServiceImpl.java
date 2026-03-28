package com.cesde.medicitas.service.ipml;

import com.cesde.medicitas.dto.ScheduleRequest;
import com.cesde.medicitas.dto.ScheduleResponse;
import com.cesde.medicitas.dto.SlotResponse;
import com.cesde.medicitas.entity.Doctor;
import com.cesde.medicitas.entity.DoctorSchedule;
import com.cesde.medicitas.exception.ResourceNotFoundException;
import com.cesde.medicitas.repository.DoctorRepository;
import com.cesde.medicitas.repository.DoctorScheduleRepository;
import com.cesde.medicitas.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final DoctorScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;

    public ScheduleResponse createSchedule(Long doctorId, ScheduleRequest req) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor no encontrado: " + doctorId));
        DoctorSchedule schedule = DoctorSchedule.builder()
                .doctor(doctor)
                .diaSemana(req.diaSemana())
                .horaInicio(req.horaInicio())
                .horaFin(req.horaFin())
                .duracionCitaMinutos(req.duracionCitaMinutos())
                .build();
        return toResponse(scheduleRepository.save(schedule));
    }

    public List<ScheduleResponse> getSchedulesByDoctor(Long doctorId) {
        return scheduleRepository.findByDoctorId(doctorId)
                .stream().map(this::toResponse).toList();
    }

    public List<SlotResponse> getAvailableSlots(Long doctorId, LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        List<DoctorSchedule> schedules =
                scheduleRepository.findByDoctorIdAndDiaSemana(doctorId, day);

        List<SlotResponse> slots = new ArrayList<>();
        LocalTime now = LocalTime.now();

        for (DoctorSchedule schedule : schedules) {
            LocalTime cursor = schedule.getHoraInicio();
            while (cursor.plusMinutes(schedule.getDuracionCitaMinutos())
                    .compareTo(schedule.getHoraFin()) <= 0) {
                LocalTime end = cursor.plusMinutes(schedule.getDuracionCitaMinutos());
                // No mostrar slots pasados si la fecha es hoy
                boolean esPasado = date.equals(LocalDate.now()) && cursor.isBefore(now);
                if (!esPasado) {
                    slots.add(new SlotResponse(cursor, end, true));
                }
                cursor = end;
            }
        }
        return slots;
    }

    private ScheduleResponse toResponse(DoctorSchedule s) {
        return new ScheduleResponse(s.getId(), s.getDiaSemana(),
                s.getHoraInicio(), s.getHoraFin(), s.getDuracionCitaMinutos());
    }
}