package com.alisimsek.service.impl;

import com.alisimsek.converter.training.TrainingConverter;
import com.alisimsek.dto.request.TrainingRequest;
import com.alisimsek.dto.request.TrainingSearchRequest;
import com.alisimsek.dto.request.UpdateTrainingRequest;
import com.alisimsek.dto.response.TrainingResponse;
import com.alisimsek.exception.ExceptionMessage;
import com.alisimsek.exception.customException.EntityAlreadyExistsException;
import com.alisimsek.exception.customException.EntityNotFoundException;
import com.alisimsek.model.Trainee;
import com.alisimsek.model.Trainer;
import com.alisimsek.model.Training;
import com.alisimsek.model.TrainingType;
import com.alisimsek.repository.TrainingRepository;
import com.alisimsek.service.TraineeService;
import com.alisimsek.service.TrainerService;
import com.alisimsek.service.TrainingService;
import com.alisimsek.service.TrainingTypeService;
import com.alisimsek.specification.TrainingSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TrainerService trainerService;
    private final TraineeService traineeService;
    private final TrainingTypeService trainingTypeService;
    private final TrainingConverter trainingConverter;

    @Override
    public void createTraining(TrainingRequest createRequest) {
        log.info("Creating new training");

        Trainer trainer = trainerService.getActiveTrainerByUsername(createRequest.trainerUsername());
        Trainee trainee = traineeService.getActiveTraineeByUsername(createRequest.traineeUsername());
        TrainingType trainingType = trainingTypeService.getTrainingTypeById(createRequest.trainingTypeId());

        validateTrainingDoesNotExist(trainer, trainee, trainingType, createRequest.trainingDate());

        Training newTraining = buildTraining(createRequest, trainer, trainee, trainingType);

        traineeService.addTrainerToTrainee(trainee, trainer);

        trainingConverter.toTrainingResponse(trainingRepository.save(newTraining));
        log.info("New training created successfully");
    }

    private void validateTrainingDoesNotExist(Trainer trainer, Trainee trainee, TrainingType type, LocalDate date) {
        trainingRepository.findByTrainerIdAndTraineeIdAndTrainingTypeIdAndTrainingDate(trainer.getId(), trainee.getId(), type.getId(), date)
                .ifPresent(existing -> {
            throw new EntityAlreadyExistsException();
        });
    }

    private Training buildTraining(TrainingRequest request, Trainer trainer, Trainee trainee, TrainingType trainingType) {
        Training training = new Training();
        training.setTrainingName(request.trainingName());
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setTrainingType(trainingType);
        training.setTrainingDate(request.trainingDate());
        training.setTrainingDuration(request.trainingDuration());
        return training;
    }

    @Override
    public TrainingResponse updateTraining(Long id, UpdateTrainingRequest updateTrainingRequest) {
        log.info("Updating training with id: {}", id);

        Trainee trainee = traineeService.getActiveTraineeByUsername(updateTrainingRequest.traineeUsername());

        Training trainingFromStorage = trainingRepository.findByIdAndTraineeId(id, trainee.getId())
                .orElseThrow(() -> new EntityNotFoundException(Training.class.getSimpleName()));

        Trainer trainer = trainerService.getActiveTrainerByUsername(updateTrainingRequest.trainerUsername());

        Trainer oldTrainer = trainingFromStorage.getTrainer();
        removeTrainerIf(trainee, oldTrainer);

        trainingFromStorage.setTrainer(trainer);
        traineeService.addTrainerToTrainee(trainee, trainer);

        return trainingConverter.toTrainingResponse(trainingRepository.save(trainingFromStorage));
    }

    @Override
    public TrainingResponse getTrainingById(Long id) {

        log.info("Retrieving training with id: {}", id);

        Optional<Training> trainingFromStorageOptional = trainingRepository.findById(id);

        if (trainingFromStorageOptional.isEmpty()) {
            log.error(ExceptionMessage.getEntityNotFoundMessage("Training", id));
            return null;
        }

        return trainingConverter.toTrainingResponse(trainingFromStorageOptional.get());
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

        trainingRepository.delete(training);
        log.info("Training with id {} deleted.", id);
    }

    @Override
    public List<TrainingResponse> searchTraining(TrainingSearchRequest trainingSearchRequest) {

        List<Training> trainings = trainingRepository.findAll(TrainingSpecification.search(trainingSearchRequest));

        if (Objects.nonNull(trainingSearchRequest.getTraineeUsername())) {
            log.info("Retrieving all trainee's trainings for {}", trainingSearchRequest.getTraineeUsername());
            return trainings.stream().map(trainingConverter::toTrainingResponse).toList();
        } else {
            log.info("Retrieving all trainer's trainings for {}", trainingSearchRequest.getTrainerUsername());
            return trainings.stream().map(trainingConverter::toTrainingResponse).toList();
        }
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
}
