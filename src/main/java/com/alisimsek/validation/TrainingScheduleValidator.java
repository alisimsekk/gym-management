package com.alisimsek.validation;

import com.alisimsek.constant.TrainingScheduleConstants;
import com.alisimsek.exception.customException.InvalidTrainingTimeSlotException;
import com.alisimsek.exception.customException.TrainingDurationExceededException;
import com.alisimsek.exception.customException.TrainingPastDateTimeException;

import java.time.LocalDateTime;
import java.time.LocalTime;

public final class TrainingScheduleValidator {

    private TrainingScheduleValidator() {}

    public static void validateTrainingDateTime(LocalDateTime trainingDateTime) {
        if (trainingDateTime == null) {
            throw new InvalidTrainingTimeSlotException();
        }
        if (!isOnTheHour(trainingDateTime)) {
            throw new InvalidTrainingTimeSlotException();
        }
        if (!TrainingScheduleConstants.ALLOWED_HOURS.contains(trainingDateTime.getHour())) {
            throw new InvalidTrainingTimeSlotException();
        }
        if (isPast(trainingDateTime)) {
            throw new TrainingPastDateTimeException();
        }
    }

    public static void validateDuration(Integer trainingDuration) {
        if (trainingDuration == null || trainingDuration < 1) {
            throw new TrainingDurationExceededException();
        }
        if (trainingDuration > TrainingScheduleConstants.MAX_DURATION_MINUTES) {
            throw new TrainingDurationExceededException();
        }
    }

    public static boolean isOnTheHour(LocalDateTime dateTime) {
        return dateTime.getMinute() == 0 && dateTime.getSecond() == 0 && dateTime.getNano() == 0;
    }

    public static boolean isAllowedHour(int hour) {
        return TrainingScheduleConstants.ALLOWED_HOURS.contains(hour);
    }

    public static boolean isPast(LocalDateTime dateTime) {
        return dateTime.isBefore(LocalDateTime.now(TrainingScheduleConstants.ZONE));
    }

    public static LocalDateTime slotAt(LocalDateTime date, int hour) {
        return LocalDateTime.of(date.toLocalDate(), LocalTime.of(hour, 0));
    }
}
