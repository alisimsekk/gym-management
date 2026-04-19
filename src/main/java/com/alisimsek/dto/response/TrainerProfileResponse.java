package com.alisimsek.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record TrainerProfileResponse(
    String firstName,
    String lastName,
    String specialization,
    boolean isActive,
    List<TraineeBasicInfoDto> trainees
) {
}