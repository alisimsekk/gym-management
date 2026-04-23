package com.alisimsek.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrainingSearchRequest {

    private String traineeUsername;
    private LocalDate periodFrom;
    private LocalDate periodTo;
    private String trainerName;
    private String trainingTypeName;
    private String trainerUsername;
    private String traineeName;
}
