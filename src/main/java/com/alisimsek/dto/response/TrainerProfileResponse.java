package com.alisimsek.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record TrainerProfileResponse(
        Long id,
        String username,
        String firstName,
        String lastName,
        String email,
        String specialization,
        boolean isActive,
        List<TraineeBasicInfoDto> trainees) {
}