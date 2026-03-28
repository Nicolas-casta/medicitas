package com.cesde.medicitas.dto;

import com.cesde.medicitas.enums.Role;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String identityDocument;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private Role role;
}
