package com.cesde.medicitas.dto;

import java.time.LocalDate;

public record PatientResponse(
        Long id,
        String nombre,
        String apellido,
        String documento,
        String email,
        String telefono,
        LocalDate fechaNacimiento,
        String direccion,
        String eps,
        String tipoSangre
) {}