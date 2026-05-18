package com.alisimsek.dto.response;

import com.alisimsek.enums.SlotUnavailabilityReason;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TrainingTimeSlotDto(
        String label,
        LocalDateTime dateTime,
        boolean available,
        SlotUnavailabilityReason reason
) {}
