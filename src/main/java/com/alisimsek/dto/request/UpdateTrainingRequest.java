package com.alisimsek.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record UpdateTrainingRequest(
        @NotBlank(message = "Training name is required")
        String trainingName,

        @NotBlank(message = "Trainee username is required")
        String traineeUsername,

        @NotBlank(message = "Trainer username is required")
        String trainerUsername,

        @NotNull(message = "Training type ID is required")
        Long trainingTypeId,

        @NotNull(message = "Training date and time is required")
        LocalDateTime trainingDateTime,

        @NotNull(message = "Training duration is required")
        @Min(value = 1, message = "Training duration must be at least 1 minute")
        @Max(value = 45, message = "Training duration cannot exceed 45 minutes")
        Integer trainingDuration
) {}
