package com.alisimsek.dto.response;

import lombok.Builder;

import java.time.Month;

@Builder
public record MonthlyWorkload(
        Month month,
        Integer totalTrainingDuration) {
}
