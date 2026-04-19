package com.alisimsek.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateTrainerRequest(

    @NotBlank(message = "First name is required")
    String firstName,

    @NotBlank(message = "Last name is required")
    String lastName,

    @NotNull(message = "SpecializationId is required")
    Long specializationId,

    @NotNull(message = "Active status is required")
    Boolean isActive
) {}