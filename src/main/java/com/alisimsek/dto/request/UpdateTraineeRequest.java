package com.alisimsek.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateTraineeRequest(

    @NotBlank(message = "First name is required")
    String firstName,

    @NotBlank(message = "Last name is required")
    String lastName,

    LocalDate dateOfBirth,
    String address,

    @NotNull(message = "Active status is required")
    Boolean isActive
) {}