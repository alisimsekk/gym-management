package com.alisimsek.dto.request;

import com.alisimsek.enums.ActionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
public class TrainerWorkloadRequest {
    @NotBlank
    private String trainerUsername;
    @NotBlank
    private String trainerFirstName;
    @NotBlank
    private String trainerLastName;
    private boolean active;
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate trainingDate;
    @NotNull
    private Integer trainingDuration;
    @NotBlank
    private ActionType actionType;
}