package com.cesde.medicitas.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpecialtyRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;
}