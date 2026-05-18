package com.alisimsek.service.impl;

import com.alisimsek.constant.TrainingScheduleConstants;
import com.alisimsek.dto.response.TrainingTimeSlotDto;
import com.alisimsek.enums.SlotUnavailabilityReason;
import com.alisimsek.exception.customException.TraineeScheduleConflictException;
import com.alisimsek.exception.customException.TrainerScheduleConflictException;
import com.alisimsek.model.Trainee;
import com.alisimsek.model.Trainer;
import com.alisimsek.repository.TrainingRepository;
import com.alisimsek.service.TrainingSlotAvailabilityService;
import com.alisimsek.validation.TrainingScheduleValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingSlotAvailabilityServiceImpl implements TrainingSlotAvailabilityService {

    private static final DateTimeFormatter SLOT_LABEL = DateTimeFormatter.ofPattern("HH:mm");

    private final TrainingRepository trainingRepository;

    @Override
    public List<TrainingTimeSlotDto> computeSlots(
            Trainer trainer,
            Trainee trainee,
            LocalDate date,
            Long excludeTrainingId) {

        log.info("Computing available slots for trainer: {} and trainee: {} on date: {}", trainer.getUsername(), trainee.getUsername(), date);
        BusySlotSets busy = loadBusySlotSets(trainer.getId(), trainee.getId(), date, excludeTrainingId);

        List<TrainingTimeSlotDto> slots = new ArrayList<>();
        for (int hour : TrainingScheduleConstants.ALLOWED_HOURS) {
            LocalDateTime slotDateTime = TrainingScheduleValidator.slotAt(date.atStartOfDay(), hour);
            slots.add(buildSlotDto(slotDateTime, busy));
        }
        log.info("Available slots computed for trainer: {} and trainee: {} on date: {}", trainer.getUsername(), trainee.getUsername(), date);
        return slots;
    }

    @Override
    public void assertSlotAvailable(
            Trainer trainer,
            Trainee trainee,
            LocalDateTime trainingDateTime,
            Long excludeTrainingId) {
        LocalDate date = trainingDateTime.toLocalDate();
        BusySlotSets busy = loadBusySlotSets(trainer.getId(), trainee.getId(), date, excludeTrainingId);
        LocalDateTime normalized = normalize(trainingDateTime);

        if (busy.trainerBusy().contains(normalized)) {
            throw new TrainerScheduleConflictException();
        }
        if (busy.traineeBusy().contains(normalized)) {
            throw new TraineeScheduleConflictException();
        }
    }

    private TrainingTimeSlotDto buildSlotDto(LocalDateTime slotDateTime, BusySlotSets busy) {
        String label = slotDateTime.format(SLOT_LABEL);
        LocalDateTime normalized = normalize(slotDateTime);

        if (!TrainingScheduleValidator.isAllowedHour(slotDateTime.getHour())) {
            return new TrainingTimeSlotDto(label, slotDateTime, false, SlotUnavailabilityReason.NOT_ALLOWED_HOUR);
        }
        if (TrainingScheduleValidator.isPast(slotDateTime)) {
            return new TrainingTimeSlotDto(label, slotDateTime, false, SlotUnavailabilityReason.PAST);
        }
        if (busy.trainerBusy().contains(normalized)) {
            return new TrainingTimeSlotDto(label, slotDateTime, false, SlotUnavailabilityReason.TRAINER_BUSY);
        }
        if (busy.traineeBusy().contains(normalized)) {
            return new TrainingTimeSlotDto(label, slotDateTime, false, SlotUnavailabilityReason.TRAINEE_BUSY);
        }
        return new TrainingTimeSlotDto(label, slotDateTime, true, null);
    }

    private BusySlotSets loadBusySlotSets(
            Long trainerId,
            Long traineeId,
            LocalDate date,
            Long excludeTrainingId) {
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.atTime(23, 59, 59);

        Set<LocalDateTime> trainerBusy = toNormalizedSet(
                trainingRepository.findTrainingDateTimesByTrainerIdAndDay(trainerId, dayStart, dayEnd));
        Set<LocalDateTime> traineeBusy = toNormalizedSet(
                trainingRepository.findTrainingDateTimesByTraineeIdAndDay(traineeId, dayStart, dayEnd));

        if (excludeTrainingId != null) {
            trainingRepository.findById(excludeTrainingId).ifPresent(training -> {
                LocalDateTime excluded = normalize(training.getTrainingDateTime());
                trainerBusy.remove(excluded);
                traineeBusy.remove(excluded);
            });
        }

        return new BusySlotSets(trainerBusy, traineeBusy);
    }

    private Set<LocalDateTime> toNormalizedSet(List<LocalDateTime> dateTimes) {
        return dateTimes.stream()
                .map(this::normalize)
                .collect(Collectors.toCollection(HashSet::new));
    }

    private LocalDateTime normalize(LocalDateTime dateTime) {
        return dateTime.withMinute(0).withSecond(0).withNano(0);
    }

    private record BusySlotSets(Set<LocalDateTime> trainerBusy, Set<LocalDateTime> traineeBusy) {}
}
