package com.cesde.medicitas.repository;

import com.cesde.medicitas.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
    boolean existsByNombre(String nombre);
    List<Specialty> findByActivoTrue();
}