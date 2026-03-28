package com.cesde.medicitas.mapper;

import com.cesde.medicitas.dto.RegisterRequest;
import com.cesde.medicitas.dto.UserResponse;
import com.cesde.medicitas.entity.User;
import com.cesde.medicitas.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(RegisterRequest request, String hashedPassword) {
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .identityDocument(request.getIdentityDocument())
                .email(request.getEmail())
                .phone(request.getPhone())
                .birthDate(request.getBirthDate())
                .password(hashedPassword)
                .role(Role.PATIENT)
                .build();
    }

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .identityDocument(user.getIdentityDocument())
                .email(user.getEmail())
                .phone(user.getPhone())
                .birthDate(user.getBirthDate())
                .role(user.getRole())
                .build();
    }
}
