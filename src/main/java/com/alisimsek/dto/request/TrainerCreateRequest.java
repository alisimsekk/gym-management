package com.alisimsek.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TrainerCreateRequest(

        @NotBlank(message = "FirstName is required")
        String firstName,

        @NotBlank(message = "LastName is required")
        String lastName,
        
        @Email(message = "Email should be valid")
        @NotBlank(message = "Email is required")
        String email,

        @NotNull(message = "SpecializationId is required")
        Long specializationId
) {}