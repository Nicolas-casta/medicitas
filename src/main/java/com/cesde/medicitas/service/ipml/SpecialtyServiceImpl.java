package com.cesde.medicitas.service.ipml;

import com.cesde.medicitas.dto.SpecialtyRequest;
import com.cesde.medicitas.dto.SpecialtyResponse;
import com.cesde.medicitas.entity.Specialty;
import com.cesde.medicitas.exception.DuplicateResourceException;
import com.cesde.medicitas.exception.ResourceNotFoundException;
import com.cesde.medicitas.repository.SpecialtyRepository;
import com.cesde.medicitas.service.SpecialtyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;

    public SpecialtyResponse create(SpecialtyRequest req) {
        if (specialtyRepository.existsByNombre(req.nombre()))
            throw new DuplicateResourceException("Especialidad ya existe: " + req.nombre());
        Specialty s = Specialty.builder()
                .nombre(req.nombre()).descripcion(req.descripcion()).activo(true).build();
        return toResponse(specialtyRepository.save(s));
    }

    public List<SpecialtyResponse> findAllActive() {
        return specialtyRepository.findByActivoTrue().stream().map(this::toResponse).toList();
    }

    public SpecialtyResponse update(Long id, SpecialtyRequest req) {
        Specialty s = specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada: " + id));
        s.setNombre(req.nombre());
        s.setDescripcion(req.descripcion());
        return toResponse(specialtyRepository.save(s));
    }

    public void deactivate(Long id) {
        Specialty s = specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada: " + id));
        s.setActivo(false);
        specialtyRepository.save(s);
    }

    private SpecialtyResponse toResponse(Specialty s) {
        return new SpecialtyResponse(s.getId(), s.getNombre(), s.getDescripcion(), s.isActivo());
    }
}
