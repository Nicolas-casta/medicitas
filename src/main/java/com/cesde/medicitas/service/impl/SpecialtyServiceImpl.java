package com.cesde.medicitas.service.impl;

import com.cesde.medicitas.dto.SpecialtyRequest;
import com.cesde.medicitas.dto.SpecialtyResponse;
import com.cesde.medicitas.entity.Specialty;
import com.cesde.medicitas.exception.DuplicateResourceException;
import com.cesde.medicitas.exception.ResourceNotFoundException;
import com.cesde.medicitas.mapper.SpecialtyMapper;
import com.cesde.medicitas.repository.SpecialtyRepository;
import com.cesde.medicitas.service.SpecialtyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;
    private final SpecialtyMapper specialtyMapper;

    @Override
    public SpecialtyResponse create(SpecialtyRequest request) {
        if (specialtyRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new DuplicateResourceException("Specialty already exists with name: " + request.getName());
        }

        Specialty specialty = specialtyMapper.toEntity(request);
        Specialty saved = specialtyRepository.save(specialty);
        return specialtyMapper.toResponse(saved);
    }

    @Override
    public List<SpecialtyResponse> findAllActive() {
        return specialtyRepository.findAllByActiveTrue()
                .stream()
                .map(specialtyMapper::toResponse)
                .toList();
    }

    @Override
    public SpecialtyResponse update(Long id, SpecialtyRequest request) {
        Specialty specialty = specialtyRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));

        boolean nameChanged = !specialty.getName().equalsIgnoreCase(request.getName().trim());
        if (nameChanged && specialtyRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new DuplicateResourceException("Specialty already exists with name: " + request.getName());
        }

        specialty.setName(request.getName().trim());
        specialty.setDescription(request.getDescription());

        Specialty updated = specialtyRepository.save(specialty);
        return specialtyMapper.toResponse(updated);
    }

    @Override
    public void deactivate(Long id) {
        Specialty specialty = specialtyRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));

        specialty.setActive(false);
        specialtyRepository.save(specialty);
    }
}