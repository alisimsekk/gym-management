package com.alisimsek.service.impl;

import com.alisimsek.converter.trainer.TrainerConverter;
import com.alisimsek.dto.request.TrainerCreateRequest;
import com.alisimsek.dto.request.UpdateTrainerRequest;
import com.alisimsek.dto.response.TrainerProfileResponse;
import com.alisimsek.dto.response.TrainerUpdateResponse;
import com.alisimsek.dto.response.UserRegistrationResponse;
import com.alisimsek.enums.UserType;
import com.alisimsek.exception.ExceptionMessage;
import com.alisimsek.exception.customException.EntityNotFoundException;
import com.alisimsek.model.Trainer;
import com.alisimsek.model.TrainingType;
import com.alisimsek.model.User;
import com.alisimsek.repository.TrainerRepository;
import com.alisimsek.service.TrainerService;
import com.alisimsek.service.TrainingTypeService;
import com.alisimsek.service.UserService;
import com.alisimsek.util.PasswordGenerator;
import com.alisimsek.util.UsernameGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;
    private final TrainingTypeService trainingTypeService;
    private final UserService userService;
    private final TrainerConverter trainerConverter;

    @Override
    public UserRegistrationResponse createTrainer(TrainerCreateRequest createRequest) {
        log.info("New trainer creating.");

        TrainingType trainingType = trainingTypeService.getTrainingTypeById(createRequest.specializationId());
        if (Objects.isNull(trainingType)) {
            throw new EntityNotFoundException(TrainingType.class.getSimpleName());
        }

        String rawPassword = passwordGenerator.generatePassword();
        Trainer trainer = buildTrainer(createRequest, trainingType, rawPassword);

        return trainerConverter.toUserRegistrationResponse(trainerRepository.save(trainer), rawPassword);
    }

    @Override
    public TrainerUpdateResponse updateTrainer(Long id, UpdateTrainerRequest updateRequest) {

        log.info("Updating trainer with id: {}", id);

        Trainer trainerFromStorage = trainerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Trainer.class.getSimpleName()));

        TrainingType trainingType = trainingTypeService.getTrainingTypeById(updateRequest.specializationId());

        trainerFromStorage.setFirstName(updateRequest.firstName());
        trainerFromStorage.setLastName(updateRequest.lastName());
        trainerFromStorage.setSpecialization(trainingType);
        trainerFromStorage.setActive(updateRequest.isActive());

        return trainerConverter.toTrainerUpdateResponse(trainerRepository.save(trainerFromStorage));
    }

    @Override
    public Trainer getrainerById(Long id) {

        log.info("Retrieving trainer with id {}", id);

        Optional<Trainer> trainerFromStorageOptional = trainerRepository.findById(id);
        if (trainerFromStorageOptional.isEmpty()) {
            log.error(ExceptionMessage.getEntityNotFoundMessage("Trainer", id));
            return null;
        }

        return trainerFromStorageOptional.get();
    }

    @Override
    public TrainerProfileResponse getTrainerProfileByUsername(String username) {
        log.info("Retrieving trainer with username {}", username);

        Optional<User> user = userService.findByUsername(username);

        Trainer trainer = checkUserIsPresentAndTrainer(user);

        return trainerConverter.toTrainerProfileResponse(trainer);
    }

    @Override
    public Trainer getActiveTrainerByUsername(String username) {
        log.info("Retrieving active trainer with username {}", username);
        return trainerRepository.findActiveTrainerByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(Trainer.class.getSimpleName()));
    }

    @Override
    public void changeTrainerStatus(Long id) {
        userService.changeUserStatus(id);
    }

    private Trainer checkUserIsPresentAndTrainer(Optional<User> user) {
        if (user.isEmpty() || !(user.get() instanceof Trainer trainer)) {
            throw new EntityNotFoundException(Trainer.class.getSimpleName());
        }
        return trainer;
    }

    private String getUsername(String firstName, String lastName) {
        log.info("Generating username for firstName: {} and lastName: {}", firstName, lastName);
        String username = firstName.concat(".").concat(lastName).toLowerCase();

        Long totalUserCountWithSameUsername = userService.countUserByUsername(username);

        if (totalUserCountWithSameUsername >= 1) {
            username = usernameGenerator.generateUsername(username, totalUserCountWithSameUsername);
        }
        return username;
    }

    private Trainer buildTrainer(TrainerCreateRequest createRequest, TrainingType trainingType, String rawPassword) {
        String username = getUsername(createRequest.firstName(), createRequest.lastName());

        Trainer trainer = new Trainer();
        trainer.setFirstName(createRequest.firstName());
        trainer.setLastName(createRequest.lastName());
        trainer.setUsername(username);
        trainer.setPassword(rawPassword);
        trainer.setSpecialization(trainingType);
        trainer.setUserType(UserType.TRAINER);
        return trainer;
    }
}
