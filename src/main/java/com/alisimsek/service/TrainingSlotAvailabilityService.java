package com.alisimsek.service;

import com.alisimsek.dto.response.TrainingTimeSlotDto;
import com.alisimsek.model.Trainee;
import com.alisimsek.model.Trainer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TrainingSlotAvailabilityService {

    List<TrainingTimeSlotDto> computeSlots(Trainer trainer, Trainee trainee, LocalDate date, Long excludeTrainingId);

    void assertSlotAvailable(Trainer trainer, Trainee trainee, LocalDateTime trainingDateTime, Long excludeTrainingId);
}
