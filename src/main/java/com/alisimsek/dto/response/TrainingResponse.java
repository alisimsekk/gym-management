package com.alisimsek.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TrainingResponse(
        Long id,
        String trainingName,
        LocalDate trainingDate,
        String trainingType,
        Integer duration,
        String trainerName,
        String trainerUsername,
        String traineeName,
        String traineeUsername) {
}