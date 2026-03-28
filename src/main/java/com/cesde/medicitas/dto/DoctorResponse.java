package com.cesde.medicitas.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorResponse {

    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String identityDocument;
    private String email;
    private String phone;
    private Long specialtyId;
    private String specialtyName;
    private String licenseNumber;
    private boolean active;
}