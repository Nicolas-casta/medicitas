package com.cesde.medicitas.service;

import com.cesde.medicitas.dto.SpecialtyRequest;
import com.cesde.medicitas.dto.SpecialtyResponse;

import java.util.List;

public interface SpecialtyService {
    SpecialtyResponse create(SpecialtyRequest request);
    List<SpecialtyResponse> findAllActive();
    SpecialtyResponse update(Long id, SpecialtyRequest request);
    void deactivate(Long id);
}