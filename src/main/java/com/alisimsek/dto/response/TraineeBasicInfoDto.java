package com.alisimsek.dto.response;

import lombok.Builder;

@Builder
public record TraineeBasicInfoDto(
        String username,
        String firstName,
        String lastName) {
}
