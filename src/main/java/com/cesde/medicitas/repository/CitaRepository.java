package com.cesde.medicitas.repository;

import com.cesde.medicitas.entity.Cita;
import com.cesde.medicitas.enums.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    boolean existsByDoctorIdAndFechaAndHoraInicio(Long doctorId, LocalDate fecha, LocalTime horaInicio);
    boolean existsByPatientIdAndFechaAndHoraInicio(Long patientId, LocalDate fecha, LocalTime horaInicio);

    @Query("SELECT c.horaInicio FROM Cita c WHERE c.doctor.id = :doctorId AND c.fecha = :fecha")
    List<LocalTime> findHorasOcupadasByDoctorAndFecha(@Param("doctorId") Long doctorId,
                                                      @Param("fecha") LocalDate fecha);

    // US-016: Historial del paciente
    List<Cita> findByPatientIdOrderByFechaDescHoraInicioDesc(Long patientId);
    List<Cita> findByPatientIdAndEstadoOrderByFechaDescHoraInicioDesc(Long patientId, EstadoCita estado);

    @Query("SELECT c FROM Cita c WHERE c.patient.id = :patientId " +
           "AND c.fecha BETWEEN :desde AND :hasta ORDER BY c.fecha DESC, c.horaInicio DESC")
    List<Cita> findByPatientIdAndFechaBetween(@Param("patientId") Long patientId,
                                               @Param("desde") LocalDate desde,
                                               @Param("hasta") LocalDate hasta);

    // US-017: Agenda del doctor hoy
    List<Cita> findByDoctorIdAndFechaOrderByHoraInicio(Long doctorId, LocalDate fecha);

    // US-018/019: Reportes
    List<Cita> findByFechaBetween(LocalDate inicio, LocalDate fin);

    @Query("SELECT c FROM Cita c WHERE c.doctor.id = :doctorId AND c.fecha BETWEEN :inicio AND :fin")
    List<Cita> findByDoctorIdAndFechaBetween(@Param("doctorId") Long doctorId,
                                              @Param("inicio") LocalDate inicio,
                                              @Param("fin") LocalDate fin);
}
