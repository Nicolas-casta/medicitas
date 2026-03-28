package com.cesde.medicitas.service;

import com.cesde.medicitas.dto.*;
import java.util.List;

public interface DoctorService {
    DoctorResponse create(DoctorRequest request);
    List<DoctorResponse> findAll();
    List<DoctorResponse> findBySpecialty(Long specialtyId);
    DoctorResponse update(Long id, DoctorUpdateRequest request);
    void deactivate(Long id);
}