package com.alisimsek.dto.response;

import lombok.Builder;

@Builder
public record TrainerBasicInfoDto(
        String username,
        String firstName,
        String lastName,
        String specialization
) {
}
