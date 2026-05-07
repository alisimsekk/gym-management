package com.alisimsek.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingTypeRequest {

    @NotBlank(message = "Training type name cannot be blank")
    private String trainingName;
}
