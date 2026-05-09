package com.alisimsek.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateTrainerRequest(

    @NotBlank(message = "First name is required")
    String firstName,

    @NotBlank(message = "Last name is required")
    String lastName,
    
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    String email,

    @NotNull(message = "SpecializationId is required")
    Long specializationId,

    @NotNull(message = "Active status is required")
    Boolean isActive
) {}