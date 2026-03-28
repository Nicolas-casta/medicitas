package com.cesde.medicitas.repository;

import com.cesde.medicitas.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findBySpecialtyIdAndActivoTrue(Long specialtyId);
    boolean existsByLicenciaMedica(String licencia);
}