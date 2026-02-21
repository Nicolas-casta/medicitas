package com.cesde.medicitas.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "usuarios")
@Getter @Setter
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(unique = true, nullable = false)
    private String documentoIdentidad;

    @Column(unique = true, nullable = false)
    private String email;

    private String telefono;
    private LocalDate fechaNacimiento;

    @Column(nullable = false)
    private String password;

    private String rol = "PACIENTE";
}
