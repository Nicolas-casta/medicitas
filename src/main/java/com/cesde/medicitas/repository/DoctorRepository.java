package com.cesde.medicitas.repository;

import com.cesde.medicitas.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findAllByActiveTrue();

    List<Doctor> findAllBySpecialtyIdAndActiveTrue(Long specialtyId);

    Optional<Doctor> findByIdAndActiveTrue(Long id);

    boolean existsByLicenseNumber(String licenseNumber);

    boolean existsByUserId(Long userId);
}