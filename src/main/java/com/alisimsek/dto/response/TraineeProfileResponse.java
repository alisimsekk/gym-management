package com.alisimsek.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record TraineeProfileResponse(
    String firstName,
    String lastName,
    LocalDate dateOfBirth,
    String address,
    boolean isActive,
    List<TrainerBasicInfoDto> trainers
) {
}