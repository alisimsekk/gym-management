package com.alisimsek.dto.response;

import lombok.Builder;

@Builder
public record AdminProfileResponse(
        Long id,
        String username,
        String firstName,
        String lastName,
        String email,
        boolean isActive
) {
}
