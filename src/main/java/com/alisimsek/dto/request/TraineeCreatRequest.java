package com.alisimsek.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record TraineeCreatRequest(

        @NotBlank(message = "FirstName is required")
        String firstName,

        @NotBlank(message = "LastName is required")
        String lastName,

        @Schema(example = "1995-08-30", description = "Format: yyyy-MM-dd")
        LocalDate dateOfBirth,
        String address
) {}
