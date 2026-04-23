package com.alisimsek.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateTrainingRequest(
        @NotBlank(message = "Trainee username is required")
        String traineeUsername,

        @NotBlank(message = "Trainer username is required")
        String trainerUsername
) {}
