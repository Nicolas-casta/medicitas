package com.cesde.medicitas.repository;

import com.cesde.medicitas.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findBySpecialtyIdAndActivoTrue(Long specialtyId);
    boolean existsByLicenciaMedica(String licencia);
    Optional<Doctor> findByUserId(Long userId);
    List<Doctor> findByActivoTrue();
}
