package com.alisimsek.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record TrainerWorkloadSummary(
        String trainerUsername,
        String trainerFirstName,
        String trainerLastName,
        boolean active,
        List<YearlyWorkload> yearlyWorkloads
) {
}
