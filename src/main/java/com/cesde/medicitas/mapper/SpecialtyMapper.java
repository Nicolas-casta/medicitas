package com.cesde.medicitas.mapper;

import com.cesde.medicitas.dto.SpecialtyRequest;
import com.cesde.medicitas.dto.SpecialtyResponse;
import com.cesde.medicitas.entity.Specialty;
import org.springframework.stereotype.Component;

@Component
public class SpecialtyMapper {

    public Specialty toEntity(SpecialtyRequest request) {
        return Specialty.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .build();
    }

    public SpecialtyResponse toResponse(Specialty specialty) {
        return SpecialtyResponse.builder()
                .id(specialty.getId())
                .name(specialty.getName())
                .description(specialty.getDescription())
                .active(specialty.isActive())
                .createdAt(specialty.getCreatedAt())
                .updatedAt(specialty.getUpdatedAt())
                .build();
    }
}