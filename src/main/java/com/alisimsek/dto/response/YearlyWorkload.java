package com.alisimsek.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record YearlyWorkload(
        Integer year,
        List<MonthlyWorkload> monthlyWorkloads
){
}
