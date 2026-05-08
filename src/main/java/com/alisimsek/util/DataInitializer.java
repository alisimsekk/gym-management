package com.alisimsek.util;

import com.alisimsek.dto.request.TrainingRequest;
import com.alisimsek.enums.UserType;
import com.alisimsek.model.Admin;
import com.alisimsek.model.Trainee;
import com.alisimsek.model.Trainer;
import com.alisimsek.model.TrainingType;
import com.alisimsek.repository.AdminRepository;
import com.alisimsek.repository.TraineeRepository;
import com.alisimsek.repository.TrainerRepository;
import com.alisimsek.repository.TrainingTypeRepository;
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


            trainingDtos.forEach(trainingService::createTraining);

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
            String password,
            boolean active,
            Long specializationId,
            UserType userType
    ) {}
}