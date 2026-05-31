package com.alisimsek.service.impl;

import com.alisimsek.converter.training.TrainingConverter;
import com.alisimsek.dto.request.TrainerWorkloadRequest;
import com.alisimsek.dto.request.TrainingRequest;
import com.alisimsek.dto.request.TrainingSearchRequest;
import com.alisimsek.dto.request.UpdateTrainingRequest;
import com.alisimsek.dto.response.AvailableTrainingSlotsResponse;
import com.alisimsek.dto.response.TrainingResponse;
import com.alisimsek.enums.ActionType;
import com.alisimsek.enums.UserType;
import com.alisimsek.exception.customException.EntityNotFoundException;
import com.alisimsek.messaging.TrainerWorkloadMessageProducer;
import com.alisimsek.model.*;
import com.alisimsek.repository.TrainingRepository;
import com.alisimsek.service.TraineeService;
import com.alisimsek.service.TrainerService;
import com.alisimsek.service.TrainingService;
import com.alisimsek.service.TrainingSlotAvailabilityService;
import com.alisimsek.service.TrainingTypeService;
import com.alisimsek.specification.TrainingSpecification;
import com.alisimsek.validation.TrainingScheduleValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TrainerService trainerService;
    private final TraineeService traineeService;
    private final TrainingTypeService trainingTypeService;
    private final TrainingConverter trainingConverter;
    private final TrainerWorkloadMessageProducer trainerWorkloadMessageProducer;
    private final TrainingSlotAvailabilityService trainingSlotAvailabilityService;

    @Override
    public void createTraining(TrainingRequest createRequest) {
        log.info("Creating new training");

        Trainer trainer = trainerService.getActiveTrainerByUsername(createRequest.trainerUsername());
        Trainee trainee = traineeService.getActiveTraineeByUsername(createRequest.traineeUsername());

        isUserAuthorized(trainee, trainer);

        TrainingType trainingType = trainingTypeService.getTrainingTypeById(createRequest.trainingTypeId());

        LocalDateTime trainingDateTime = createRequest.trainingDateTime();
        TrainingScheduleValidator.validateTrainingDateTime(trainingDateTime);
        TrainingScheduleValidator.validateDuration(createRequest.trainingDuration());
        trainingSlotAvailabilityService.assertSlotAvailable(trainer, trainee, trainingDateTime, null);

        Training newTraining = buildTraining(createRequest, trainer, trainee, trainingType);

        traineeService.addTrainerToTrainee(trainee, trainer);

        trainingRepository.save(newTraining);

        log.info("New training created successfully");

        publishWorkload(trainer, trainingDateTime, createRequest.trainingDuration(), ActionType.ADD);
    }

    @Override
    public AvailableTrainingSlotsResponse getAvailableTrainingSlots(
            String trainerUsername,
            String traineeUsername,
            LocalDate date,
            Long excludeTrainingId) {

        log.info("Retrieving available training slots for trainer: {}, trainee: {}, date: {}", trainerUsername, traineeUsername, date );

        Trainer trainer = trainerService.getActiveTrainerByUsername(trainerUsername);
        Trainee trainee = traineeService.getActiveTraineeByUsername(traineeUsername);
        isUserAuthorized(trainee, trainer);

        return new AvailableTrainingSlotsResponse(
                date,
                trainingSlotAvailabilityService.computeSlots(trainer, trainee, date, excludeTrainingId));
    }

    @Override
    public TrainingResponse updateTraining(Long id, UpdateTrainingRequest updateTrainingRequest) {
        log.info("Updating training with id: {}", id);

        Trainee trainee = traineeService.getActiveTraineeByUsername(updateTrainingRequest.traineeUsername());
        Trainer trainer = trainerService.getActiveTrainerByUsername(updateTrainingRequest.trainerUsername());
        TrainingType trainingType = trainingTypeService.getTrainingTypeById(updateTrainingRequest.trainingTypeId());

        isUserAuthorized(trainee, trainer);

        Training trainingFromStorage = trainingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Training.class.getSimpleName()));

        LocalDateTime trainingDateTime = updateTrainingRequest.trainingDateTime();
        TrainingScheduleValidator.validateTrainingDateTime(trainingDateTime);
        TrainingScheduleValidator.validateDuration(updateTrainingRequest.trainingDuration());
        trainingSlotAvailabilityService.assertSlotAvailable(trainer, trainee, trainingDateTime, id);

        Trainer oldTrainer = trainingFromStorage.getTrainer();
        removeTrainerIf(trainingFromStorage.getTrainee(), oldTrainer);

        trainingFromStorage.setTrainee(trainee);
        trainingFromStorage.setTrainer(trainer);
        trainingFromStorage.setTrainingName(updateTrainingRequest.trainingName());
        trainingFromStorage.setTrainingType(trainingType);
        trainingFromStorage.setTrainingDateTime(trainingDateTime);
        trainingFromStorage.setTrainingDuration(updateTrainingRequest.trainingDuration());

        traineeService.addTrainerToTrainee(trainee, trainer);

        Training saved = trainingRepository.save(trainingFromStorage);
        return trainingConverter.toTrainingResponse(saved);
    }

    @Override
    public TrainingResponse getTrainingById(Long id) {
        log.info("Retrieving training with id: {}", id);

        Training trainingFromStorage = trainingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Training.class.getSimpleName()));

        isUserAuthorized(trainingFromStorage.getTrainee(), trainingFromStorage.getTrainer());

        return trainingConverter.toTrainingResponse(trainingFromStorage);
    }

    @Override
    public List<TrainingResponse> getAllTrainings() {
        log.info("Retrieving all trainings");
        return trainingRepository.findAll().stream().map(trainingConverter::toTrainingResponse).toList();
    }

    @Override
    public void deleteTraining(Long id) {
        log.info("Deleting training with id: {}", id);

        Training training = trainingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(Training.class.getSimpleName()));

        isUserAuthorized(training.getTrainee(), training.getTrainer());

        Trainer trainer = training.getTrainer();
        LocalDateTime trainingDateTime = training.getTrainingDateTime();
        Integer duration = training.getTrainingDuration();

        trainingRepository.delete(training);
        log.info("Training with id {} deleted.", id);

        publishWorkload(trainer, trainingDateTime, duration, ActionType.DELETE);
    }

    @Override
    public List<TrainingResponse> searchTraining(TrainingSearchRequest trainingSearchRequest) {
        log.info("Retrieving searched trainings");

        User authenticatedUser = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        UserType authenticatedUserUserType = authenticatedUser.getUserType();

        if (UserType.TRAINEE.equals(authenticatedUserUserType)) {
            trainingSearchRequest.setTraineeUsername(authenticatedUser.getUsername());
        } else if (UserType.TRAINER.equals(authenticatedUserUserType)) {
            trainingSearchRequest.setTrainerUsername(authenticatedUser.getUsername());
        }

        List<Training> trainings = trainingRepository.findAll(TrainingSpecification.search(trainingSearchRequest));

        return trainings.stream().map(trainingConverter::toTrainingResponse).toList();
    }

    private Training buildTraining(
            TrainingRequest request,
            Trainer trainer,
            Trainee trainee,
            TrainingType trainingType) {
        Training training = new Training();
        training.setTrainingName(request.trainingName());
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setTrainingType(trainingType);
        training.setTrainingDateTime(request.trainingDateTime());
        training.setTrainingDuration(request.trainingDuration());
        return training;
    }

    private void publishWorkload(
            Trainer trainer,
            LocalDateTime trainingDateTime,
            Integer trainingDuration,
            ActionType actionType) {
        log.info("Sending Workload request for trainer: {}", trainer.getUsername());
        TrainerWorkloadRequest workloadRequest = buildTrainerWorkloadRequest(
                trainer, trainingDateTime.toLocalDate(), trainingDuration, actionType);
        trainerWorkloadMessageProducer.publishTrainerWorkload(workloadRequest);
        log.info("Workload request sent asynchronously.");
    }

    private void removeTrainerIf(Trainee trainee, Trainer oldTrainer) {
        if (hasSingleTrainingWithTrainer(trainee, oldTrainer)) {
            trainee.getAssignedTrainers().remove(oldTrainer);
        }
    }

    private boolean hasSingleTrainingWithTrainer(Trainee trainee, Trainer oldTrainer) {
        if (trainee.getTrainings() != null && !trainee.getTrainings().isEmpty()) {
            long count = trainee.getTrainings().stream()
                    .filter(training -> training.getTrainer().equals(oldTrainer))
                    .count();
            return count == 1;
        }
        return false;
    }

    private TrainerWorkloadRequest buildTrainerWorkloadRequest(
            Trainer trainer,
            LocalDate trainingDate,
            Integer trainingDuration,
            ActionType actionType) {
        return TrainerWorkloadRequest.builder()
                .trainerUsername(trainer.getUsername())
                .trainerFirstName(trainer.getFirstName())
                .trainerLastName(trainer.getLastName())
                .active(trainer.isActive())
                .trainingDate(trainingDate)
                .trainingDuration(trainingDuration)
                .actionType(actionType)
                .build();
    }

    private void isUserAuthorized(Trainee trainee, Trainer trainer) {
        User authenticatedUser = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        assert authenticatedUser != null;
        String authenticatedUserUsername = authenticatedUser.getUsername();
        if (UserType.ADMIN.equals(authenticatedUser.getUserType())) {
            return;
        }
        boolean isParticipant = authenticatedUserUsername.equals(trainee.getUsername())
                || authenticatedUserUsername.equals(trainer.getUsername());
        if (!isParticipant) {
            throw new AccessDeniedException("Access denied. You are not authorized to update this training.");
        }
    }
}
