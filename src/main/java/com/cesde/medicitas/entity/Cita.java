package com.cesde.medicitas.entity;

import com.cesde.medicitas.enums.EstadoCita;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "citas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "motivo_consulta", nullable = false)
    private String motivoConsulta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoCita estado = EstadoCita.AGENDADA;

    // US-013: Cancelación
    @Column(name = "motivo_cancelacion")
    private String motivoCancelacion;

    @Column(name = "fecha_cancelacion")
    private LocalDateTime fechaCancelacion;

    // US-014: Confirmación de llegada
    @Column(name = "hora_llegada")
    private LocalTime horaLlegada;

    // US-015: Atención médica
    private String diagnostico;
    private String observaciones;
    private String indicaciones;

    @Column(name = "hora_inicio_atencion")
    private LocalTime horaInicioAtencion;

    @Column(name = "hora_fin_atencion")
    private LocalTime horaFinAtencion;
}
