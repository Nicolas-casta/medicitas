package com.cesde.medicitas.repository;

import com.cesde.medicitas.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {

    List<Specialty> findAllByActiveTrue();

    boolean existsByNameIgnoreCase(String name);

    Optional<Specialty> findByIdAndActiveTrue(Long id);
}