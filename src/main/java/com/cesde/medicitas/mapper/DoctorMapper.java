package com.cesde.medicitas.mapper;

import com.cesde.medicitas.dto.DoctorResponse;
import com.cesde.medicitas.entity.Doctor;
import org.springframework.stereotype.Component;

@Component
public class DoctorMapper {

    public DoctorResponse toResponse(Doctor doctor) {
        return DoctorResponse.builder()
                .id(doctor.getId())
                .userId(doctor.getUser().getId())
                .firstName(doctor.getUser().getFirstName())
                .lastName(doctor.getUser().getLastName())
                .identityDocument(doctor.getUser().getIdentityDocument())
                .email(doctor.getUser().getEmail())
                .phone(doctor.getUser().getPhone())
                .specialtyId(doctor.getSpecialty().getId())
                .specialtyName(doctor.getSpecialty().getName())
                .licenseNumber(doctor.getLicenseNumber())
                .active(doctor.isActive())
                .build();
    }
}