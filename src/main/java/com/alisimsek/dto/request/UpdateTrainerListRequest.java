package com.alisimsek.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record UpdateTrainerListRequest(
        @NotEmpty(message = "Trainer username list required.")
        List<String> trainerUsernames
) {}
