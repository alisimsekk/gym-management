package com.alisimsek.dto.response;

import lombok.Builder;

@Builder
public record UserRegistrationResponse(
    String username,
    String password
) {}