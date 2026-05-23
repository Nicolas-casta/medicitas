package com.cesde.medicitas.service;

import com.cesde.medicitas.dto.*;
import com.cesde.medicitas.enums.EstadoCita;

import java.time.LocalDate;
import java.util.List;

public interface CitaService {

    // US-011: Recepcionista agenda cita
    CitaResponse agendarCita(CitaRequest request);

    // US-012: Paciente solicita cita
    CitaResponse solicitarCita(CitaRequest request, Long userId);
    SlotsDisponiblesResponse getSlotsDisponibles(Long doctorId, LocalDate fecha);

    // US-013: Cancelar cita
    CitaDetalleResponse cancelarCita(Long citaId, Long userId, String rol, CancelacionRequest request);

    // US-014: Confirmar llegada (RECEPCIONISTA/ADMIN)
    CitaDetalleResponse confirmarLlegada(Long citaId, ConfirmacionRequest request);

    // US-015: Atender cita (DOCTOR)
    CitaDetalleResponse atenderCita(Long citaId, Long userId, AtencionRequest request);

    // US-016: Historial del paciente
    List<CitaDetalleResponse> getMisCitas(Long userId, EstadoCita estado, LocalDate desde, LocalDate hasta);

    // US-017: Agenda del día del doctor
    List<CitaDetalleResponse> getAgendaHoy(Long userId);

    // Consultas admin/recepcionista
    List<CitaResponse> findByPatient(Long patientId);
    List<CitaResponse> findByDoctorAndFecha(Long doctorId, LocalDate fecha);
}
