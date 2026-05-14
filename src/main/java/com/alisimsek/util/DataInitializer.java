package com.alisimsek.util;

import com.alisimsek.dto.request.TrainerWorkloadRequest;
import com.alisimsek.dto.request.TrainingRequest;
import com.alisimsek.enums.ActionType;
import com.alisimsek.enums.UserType;
import com.alisimsek.exception.customException.EntityNotFoundException;
import com.alisimsek.messaging.TrainerWorkloadMessageProducer;
import com.alisimsek.model.*;
import com.alisimsek.repository.*;
import com.alisimsek.service.TraineeService;
import com.alisimsek.service.TrainingService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final TrainingService trainingService;
    private final AdminRepository adminRepository;
    private final TraineeService traineeService;
    private final TrainerWorkloadMessageProducer trainerWorkloadMessageProducer;
    private final TrainingRepository trainingRepository;
    private final ObjectMapper objectMapper;

    @Value("${initial.data.trainingType.file}")
    private String trainingTypeDataPath;

    @Value("${initial.data.trainer.file}")
    private String trainerDataPath;

    @Value("${initial.data.trainee.file}")
    private String traineeDataPath;

    @Value("${initial.data.training.file}")
    private String trainingDataPath;

    @Value("${initial.data.admin.file}")
    private String adminDataPath;

    @PostConstruct
    public void initData() {
        objectMapper.registerModule(new JavaTimeModule());

        if (!trainingTypeRepository.findAll().isEmpty()) {
            log.info("Sample data already exists. Skipping initialization.");
            return;
        }

        try {
            // Initialize Training Types
            List<TrainingType> trainingTypes = loadDataFromJson(trainingTypeDataPath, new TypeReference<List<TrainingType>>() {});
            Map<Long, TrainingType> savedTrainingTypes = trainingTypes.stream()
                    .map(trainingTypeRepository::save)
                    .collect(Collectors.toMap(TrainingType::getId, Function.identity()));
            log.info("Training types initialized: {}", savedTrainingTypes.size());

            // Initialize Admin
            List<Admin> admins = loadDataFromJson(adminDataPath, new TypeReference<List<Admin>>() {});
            Map<String, Admin> savedAdmins = admins.stream()
                    .map(adminRepository::save)
                    .collect(Collectors.toMap(Admin::getUsername, Function.identity()));
            log.info("Admins initialized: {}", savedAdmins.size());


            // Initialize Trainers
            List<TrainerDataDto> trainerDtos = loadDataFromJson(trainerDataPath, new TypeReference<List<TrainerDataDto>>() {});
            List<Trainer> trainers = trainerDtos.stream()
                    .map(dto -> createTrainer(dto, savedTrainingTypes))
                    .map(trainerRepository::save)
                    .toList();
            Map<String, Trainer> savedTrainers = trainers.stream()
                    .collect(Collectors.toMap(Trainer::getUsername, Function.identity()));
            log.info("Trainers initialized: {}", savedTrainers.size());

            // Initialize Trainees
            List<Trainee> trainees = loadDataFromJson(traineeDataPath, new TypeReference<List<Trainee>>() {});
            Map<String, Trainee> savedTrainees = trainees.stream()
                    .map(traineeRepository::save)
                    .collect(Collectors.toMap(Trainee::getUsername, Function.identity()));
            log.info("Trainees initialized: {}", savedTrainees.size());

            // Initialize Trainings
            List<TrainingRequest> trainingDtos = loadDataFromJson(trainingDataPath, new TypeReference<List<TrainingRequest>>() {});


            trainingDtos.forEach(this::createTraining);

            log.info("Trainings initialized.");

        } catch (Exception e) {
            log.error("Error initializing data", e);
            throw new RuntimeException("Failed to initialize data", e);
        }
    }

    private <T> T loadDataFromJson(String resourcePath, TypeReference<T> typeReference) throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            return objectMapper.readValue(inputStream, typeReference);
        }
    }

    private Trainer createTrainer(TrainerDataDto dto, Map<Long, TrainingType> trainingTypes) {
        Trainer trainer = new Trainer();
        trainer.setUsername(dto.username());
        trainer.setEmail(dto.email());
        trainer.setFirstName(dto.firstName());
        trainer.setLastName(dto.lastName());
        trainer.setPassword(dto.password());
        trainer.setActive(dto.active());
        trainer.setSpecialization(trainingTypes.get(dto.specializationId()));
        trainer.setUserType(dto.userType);
        return trainer;
    }

    // DTOs for JSON deserialization
    private record TrainerDataDto(
            String firstName,
            String lastName,
            String username,
            String email,
            String password,
            boolean active,
            Long specializationId,
            UserType userType
    ) {}

    public void createTraining(TrainingRequest createRequest) {
        log.info("Creating new training");

        Trainer trainer = trainerRepository.findActiveTrainerByUsername(createRequest.trainerUsername())
                .orElseThrow(() -> new EntityNotFoundException(Trainer.class.getSimpleName()));
        Trainee trainee = traineeRepository.findActiveTraineeByUsername(createRequest.traineeUsername())
                .orElseThrow(() -> new EntityNotFoundException(Trainee.class.getSimpleName()));

        TrainingType trainingType = trainingTypeRepository.findById(createRequest.trainingTypeId())
                .orElseThrow(() -> new EntityNotFoundException(TrainingType.class.getSimpleName()));


        Training newTraining = buildTraining(createRequest, trainer, trainee, trainingType);

        traineeService.addTrainerToTrainee(trainee, trainer);

        trainingRepository.save(newTraining);

        log.info("New training created successfully");

        log.info("Sending Workload request for trainer: {}", trainer.getUsername());

        TrainerWorkloadRequest workloadRequest = buildTrainerWorkloadRequest(trainer, createRequest.trainingDate(), createRequest.trainingDuration(), ActionType.ADD);

        trainerWorkloadMessageProducer.publishTrainerWorkload(
                workloadRequest
        );
        log.info("Workload request sent asynchronously.");
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

    private TrainerWorkloadRequest buildTrainerWorkloadRequest(Trainer trainer, LocalDate trainingDate, Integer trainingDuration, ActionType actionType) {
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
}