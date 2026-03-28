package com.cesde.medicitas.service;

import com.cesde.medicitas.dto.DoctorRequest;
import com.cesde.medicitas.dto.DoctorResponse;
import com.cesde.medicitas.dto.DoctorUpdateRequest;

import java.util.List;

public interface DoctorService {
    DoctorResponse create(DoctorRequest request);
    List<DoctorResponse> findAll(Long specialtyId);
    DoctorResponse update(Long id, DoctorUpdateRequest request);
    void deactivate(Long id);
}