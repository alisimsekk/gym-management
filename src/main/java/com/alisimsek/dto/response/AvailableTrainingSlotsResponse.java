package com.alisimsek.dto.response;

import java.time.LocalDate;
import java.util.List;

public record AvailableTrainingSlotsResponse(
        LocalDate date,
        List<TrainingTimeSlotDto> slots
) {}
