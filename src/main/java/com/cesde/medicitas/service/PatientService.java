package com.cesde.medicitas.service;

import com.cesde.medicitas.dto.PatientPerfilUpdateRequest;
import com.cesde.medicitas.dto.PatientRequest;
import com.cesde.medicitas.dto.PatientResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PatientService {

    // US-009
    PatientResponse create(PatientRequest request);
    PatientResponse findByDocumento(String documento);
    Page<PatientResponse> findAll(String nombre, Pageable pageable);
    PatientResponse update(Long id, PatientRequest request);
    void delete(Long id);

    // US-010
    PatientResponse getMyProfile(Long userId);
    PatientResponse updateMyProfile(Long userId, PatientPerfilUpdateRequest request);
}