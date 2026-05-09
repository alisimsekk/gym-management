package com.alisimsek.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record TraineeUpdateResponse(
        Long id,
        String username,
        String firstName,
        String lastName,
        String email,
        LocalDate dateOfBirth,
        String address,
        boolean isActive,
        List<TrainerBasicInfoDto> trainers
) {
}