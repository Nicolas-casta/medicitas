package com.cesde.medicitas.service.impl;

import com.cesde.medicitas.dto.*;
import com.cesde.medicitas.entity.Cita;
import com.cesde.medicitas.entity.Doctor;
import com.cesde.medicitas.entity.Patient;
import com.cesde.medicitas.enums.EstadoCita;
import com.cesde.medicitas.enums.TipoNotificacion;
import com.cesde.medicitas.exception.DuplicateResourceException;
import com.cesde.medicitas.exception.ResourceNotFoundException;
import com.cesde.medicitas.repository.CitaRepository;
import com.cesde.medicitas.repository.DoctorRepository;
import com.cesde.medicitas.repository.PatientRepository;
import com.cesde.medicitas.service.CitaService;
import com.cesde.medicitas.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final NotificacionService notificacionService;

    private static final LocalTime HORA_INICIO_JORNADA = LocalTime.of(8, 0);
    private static final LocalTime HORA_FIN_JORNADA    = LocalTime.of(17, 0);
    private static final int DURACION_SLOT_MINUTOS      = 30;

    // ─── US-011 ───────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public CitaResponse agendarCita(CitaRequest request) {
        Cita saved = crearCita(request);
        notificar(saved, TipoNotificacion.CITA_AGENDADA);
        return toResponse(saved);
    }

    // ─── US-012 ───────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public CitaResponse solicitarCita(CitaRequest request, Long userId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de paciente no encontrado para usuario: " + userId));
        if (!patient.getId().equals(request.patientId()))
            throw new SecurityException("Solo puedes agendar citas para tu propio perfil");
        Cita saved = crearCita(request);
        notificar(saved, TipoNotificacion.CITA_AGENDADA);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SlotsDisponiblesResponse getSlotsDisponibles(Long doctorId, LocalDate fecha) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor no encontrado: " + doctorId));
        List<LocalTime> ocupadas = citaRepository.findHorasOcupadasByDoctorAndFecha(doctorId, fecha);
        LocalTime now = LocalTime.now();
        List<LocalTime> disponibles = generarTodosLosSlots().stream()
                .filter(s -> !ocupadas.contains(s))
                .filter(s -> !fecha.equals(LocalDate.now()) || s.isAfter(now))
                .collect(Collectors.toList());
        String nombre = doctor.getUser().getNombre() + " " + doctor.getUser().getApellido();
        return new SlotsDisponiblesResponse(doctorId, nombre, fecha.toString(), disponibles);
    }

    // ─── US-013 ───────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public CitaDetalleResponse cancelarCita(Long citaId, Long userId, String rol, CancelacionRequest request) {
        Cita cita = getCitaOrThrow(citaId);

        if (cita.getEstado() != EstadoCita.AGENDADA)
            throw new IllegalStateException("Solo se pueden cancelar citas con estado AGENDADA");

        // Validar 24 horas de anticipación
        LocalDateTime citaDateTime = cita.getFecha().atTime(cita.getHoraInicio());
        long horasRestantes = ChronoUnit.HOURS.between(LocalDateTime.now(), citaDateTime);
        if (horasRestantes < 24)
            throw new IllegalStateException("Solo se puede cancelar con más de 24 horas de anticipación");

        // PACIENTE solo cancela sus propias citas
        if ("PACIENTE".equals(rol)) {
            Patient patient = patientRepository.findByUserId(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado para usuario: " + userId));
            if (!cita.getPatient().getId().equals(patient.getId()))
                throw new SecurityException("Solo puedes cancelar tus propias citas");
        }

        cita.setEstado(EstadoCita.CANCELADA);
        cita.setMotivoCancelacion(request.motivoCancelacion());
        cita.setFechaCancelacion(LocalDateTime.now());

        Cita saved = citaRepository.save(cita);
        notificar(saved, TipoNotificacion.CITA_CANCELADA);
        return toDetalleResponse(saved);
    }

    // ─── US-014 ───────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public CitaDetalleResponse confirmarLlegada(Long citaId, ConfirmacionRequest request) {
        Cita cita = getCitaOrThrow(citaId);

        if (cita.getEstado() != EstadoCita.AGENDADA)
            throw new IllegalStateException("Solo se puede confirmar llegada de citas AGENDADAS");

        if (!cita.getFecha().equals(LocalDate.now()))
            throw new IllegalStateException("Solo se puede confirmar llegada el mismo día de la cita");

        cita.setEstado(EstadoCita.CONFIRMADA);
        cita.setHoraLlegada(request.horaLlegada());
        return toDetalleResponse(citaRepository.save(cita));
    }

    // ─── US-015 ───────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public CitaDetalleResponse atenderCita(Long citaId, Long userId, AtencionRequest request) {
        Cita cita = getCitaOrThrow(citaId);

        if (cita.getEstado() != EstadoCita.CONFIRMADA)
            throw new IllegalStateException("Solo se pueden atender citas con estado CONFIRMADA");

        // Verificar que sea el doctor asignado
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor no encontrado para usuario: " + userId));
        if (!cita.getDoctor().getId().equals(doctor.getId()))
            throw new SecurityException("Solo el doctor asignado puede atender esta cita");

        cita.setEstado(EstadoCita.ATENDIDA);
        cita.setDiagnostico(request.diagnostico());
        cita.setObservaciones(request.observaciones());
        cita.setIndicaciones(request.indicaciones());
        cita.setHoraInicioAtencion(request.horaInicioAtencion());
        cita.setHoraFinAtencion(request.horaFinAtencion());

        return toDetalleResponse(citaRepository.save(cita));
    }

    // ─── US-016 ───────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<CitaDetalleResponse> getMisCitas(Long userId, EstadoCita estado, LocalDate desde, LocalDate hasta) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado para usuario: " + userId));

        List<Cita> citas;
        if (desde != null && hasta != null) {
            citas = citaRepository.findByPatientIdAndFechaBetween(patient.getId(), desde, hasta);
        } else if (estado != null) {
            citas = citaRepository.findByPatientIdAndEstadoOrderByFechaDescHoraInicioDesc(patient.getId(), estado);
        } else {
            citas = citaRepository.findByPatientIdOrderByFechaDescHoraInicioDesc(patient.getId());
        }
        return citas.stream().map(this::toDetalleResponse).toList();
    }

    // ─── US-017 ───────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<CitaDetalleResponse> getAgendaHoy(Long userId) {
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor no encontrado para usuario: " + userId));
        return citaRepository.findByDoctorIdAndFechaOrderByHoraInicio(doctor.getId(), LocalDate.now())
                .stream().map(this::toDetalleResponse).toList();
    }

    // ─── Consultas generales ──────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponse> findByPatient(Long patientId) {
        return citaRepository.findByPatientIdOrderByFechaDescHoraInicioDesc(patientId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponse> findByDoctorAndFecha(Long doctorId, LocalDate fecha) {
        return citaRepository.findByDoctorIdAndFechaOrderByHoraInicio(doctorId, fecha)
                .stream().map(this::toResponse).toList();
    }

    // ─── Helpers privados ─────────────────────────────────────────────────────

    private Cita crearCita(CitaRequest req) {
        Patient patient = patientRepository.findById(req.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado: " + req.patientId()));
        Doctor doctor = doctorRepository.findById(req.doctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor no encontrado: " + req.doctorId()));

        if (req.fecha().isBefore(LocalDate.now()))
            throw new IllegalArgumentException("La fecha no puede ser en el pasado");
        if (citaRepository.existsByDoctorIdAndFechaAndHoraInicio(req.doctorId(), req.fecha(), req.horaInicio()))
            throw new DuplicateResourceException("El doctor ya tiene una cita el " + req.fecha() + " a las " + req.horaInicio());
        if (citaRepository.existsByPatientIdAndFechaAndHoraInicio(req.patientId(), req.fecha(), req.horaInicio()))
            throw new DuplicateResourceException("El paciente ya tiene una cita el " + req.fecha() + " a las " + req.horaInicio());

        return citaRepository.save(Cita.builder()
                .patient(patient).doctor(doctor)
                .fecha(req.fecha()).horaInicio(req.horaInicio())
                .motivoConsulta(req.motivoConsulta())
                .build());
    }

    private void notificar(Cita cita, TipoNotificacion tipo) {
        String email = cita.getPatient().getUser().getEmail();
        String nombrePaciente = cita.getPatient().getUser().getNombre() + " " + cita.getPatient().getUser().getApellido();
        String nombreDoctor = cita.getDoctor().getUser().getNombre() + " " + cita.getDoctor().getUser().getApellido();
        String mensaje = switch (tipo) {
            case CITA_AGENDADA -> String.format("Estimado/a %s, su cita con el Dr. %s ha sido agendada para el %s a las %s.",
                    nombrePaciente, nombreDoctor, cita.getFecha(), cita.getHoraInicio());
            case CITA_CANCELADA -> String.format("Estimado/a %s, su cita con el Dr. %s del %s ha sido cancelada.",
                    nombrePaciente, nombreDoctor, cita.getFecha());
            default -> "Recordatorio de cita médica.";
        };
        notificacionService.enviar(new NotificacionRequest(
                email, tipo, mensaje,
                Map.of("citaId", cita.getId(), "fecha", cita.getFecha().toString())
        ));
    }

    private List<LocalTime> generarTodosLosSlots() {
        List<LocalTime> slots = new ArrayList<>();
        LocalTime slot = HORA_INICIO_JORNADA;
        while (slot.isBefore(HORA_FIN_JORNADA)) {
            slots.add(slot);
            slot = slot.plusMinutes(DURACION_SLOT_MINUTOS);
        }
        return slots;
    }

    private Cita getCitaOrThrow(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada: " + id));
    }

    private CitaResponse toResponse(Cita c) {
        return new CitaResponse(c.getId(), c.getPatient().getId(),
                c.getPatient().getUser().getNombre() + " " + c.getPatient().getUser().getApellido(),
                c.getDoctor().getId(),
                c.getDoctor().getUser().getNombre() + " " + c.getDoctor().getUser().getApellido(),
                c.getDoctor().getSpecialty().getNombre(),
                c.getFecha(), c.getHoraInicio(), c.getMotivoConsulta(), c.getEstado());
    }

    private CitaDetalleResponse toDetalleResponse(Cita c) {
        return new CitaDetalleResponse(
                c.getId(),
                c.getPatient().getId(),
                c.getPatient().getUser().getNombre() + " " + c.getPatient().getUser().getApellido(),
                c.getPatient().getUser().getDocumento(),
                c.getDoctor().getId(),
                c.getDoctor().getUser().getNombre() + " " + c.getDoctor().getUser().getApellido(),
                c.getDoctor().getSpecialty().getNombre(),
                c.getFecha(), c.getHoraInicio(), c.getMotivoConsulta(), c.getEstado(),
                c.getMotivoCancelacion(), c.getFechaCancelacion(),
                c.getHoraLlegada(),
                c.getDiagnostico(), c.getObservaciones(), c.getIndicaciones(),
                c.getHoraInicioAtencion(), c.getHoraFinAtencion()
        );
    }
}
