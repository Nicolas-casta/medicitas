package com.cesde.medicitas.repository;

import com.cesde.medicitas.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByUserDocumento(String documento);

    Optional<Patient> findByUserId(Long userId);

    boolean existsByUserDocumento(String documento);

    @Query("SELECT p FROM Patient p WHERE " +
            "LOWER(p.user.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) OR " +
            "LOWER(p.user.apellido) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    Page<Patient> searchByNombre(@Param("nombre") String nombre, Pageable pageable);
}