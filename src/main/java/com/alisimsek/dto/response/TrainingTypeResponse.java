package com.alisimsek.dto.response;

import lombok.Builder;

@Builder
public record TrainingTypeResponse(
        Long id,
        String trainingTypeName
) {}