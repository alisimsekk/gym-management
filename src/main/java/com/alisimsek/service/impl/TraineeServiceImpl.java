package com.alisimsek.service.impl;

import com.alisimsek.converter.trainee.TraineeConverter;
import com.alisimsek.converter.trainer.TrainerConverter;
import com.alisimsek.dto.request.*;
import com.alisimsek.dto.response.TraineeProfileResponse;
import com.alisimsek.dto.response.TraineeUpdateResponse;
import com.alisimsek.dto.response.TrainerBasicInfoDto;
import com.alisimsek.dto.response.UserRegistrationResponse;
import com.alisimsek.enums.UserType;
import com.alisimsek.exception.customException.EntityNotFoundException;
import com.alisimsek.model.Trainee;
import com.alisimsek.model.Trainer;
import com.alisimsek.model.User;
import com.alisimsek.repository.TraineeRepository;
import com.alisimsek.repository.TrainerRepository;
import com.alisimsek.service.TraineeService;
import com.alisimsek.service.UserService;
import com.alisimsek.util.PasswordGenerator;
import com.alisimsek.util.UsernameGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {

    private final TraineeRepository traineeRepository;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;
    private final UserService userService;
    private final TraineeConverter traineeConverter;
    private final TrainerRepository trainerRepository;
    private final TrainerConverter trainerConverter;

    @Override
    public UserRegistrationResponse createTrainee(TraineeCreatRequest createRequest) {
        log.info("New trainee creating.");

        String rawPassword = passwordGenerator.generatePassword();
        Trainee trainee = buildTraineeFromRequest(createRequest, rawPassword);

        return traineeConverter.toUserRegistrationResponse(traineeRepository.save(trainee), rawPassword);
    }

    @Override
    public TraineeUpdateResponse updateTrainee(Long id, UpdateTraineeRequest updateRequest) {
        log.info("Updating trainee with id: {}", id);

        Trainee traineeFromStorage = traineeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Trainee.class.getSimpleName()));

        traineeFromStorage.setFirstName(updateRequest.firstName());
        traineeFromStorage.setLastName(updateRequest.lastName());

        LocalDate dateOfBirth = Objects.nonNull(updateRequest.dateOfBirth()) ? updateRequest.dateOfBirth() : traineeFromStorage.getDateOfBirth();
        traineeFromStorage.setDateOfBirth(dateOfBirth);

        String address =Objects.nonNull(updateRequest.address()) ? updateRequest.address() : traineeFromStorage.getAddress();
        traineeFromStorage.setAddress(address);
        traineeFromStorage.setActive(updateRequest.isActive());

        return traineeConverter.toTraineeUpdateResponse(traineeRepository.save(traineeFromStorage));
    }

    @Override
    public TraineeProfileResponse getTraineeById(Long id) {

        log.info("Retrieving trainee with id {}", id);

        Trainee trainee = traineeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Trainee.class.getSimpleName()));
        return traineeConverter.toTraineeProfileResponse(trainee);
    }

    @Override
    public void deleteTraineeByUsername(String username) {
        Optional<User> user = userService.findByUsername(username);

        Trainee trainee = checkUserIsPresentAndTrainee(user);

        traineeRepository.delete(trainee);

        log.info("Trainee with username {} deleted.", username);
    }

    @Override
    public TraineeProfileResponse getTraineeProfileByUsername(String username) {
        Optional<User> user = userService.findByUsername(username);

        Trainee trainee = checkUserIsPresentAndTrainee(user);

        return traineeConverter.toTraineeProfileResponse(trainee);
    }

    @Override
    public void changeTraineeStatus(Long userId) {
        userService.changeUserStatus(userId);
    }

    @Override
    public List<TrainerBasicInfoDto> updateTrainerList(String username, UpdateTrainerListRequest request) {

        Trainee trainee = checkUserIsPresentAndTrainee(userService.findByUsername(username));

        List<String> newTrainerUsernames = request.trainerUsernames();

        List<Trainer> newTrainers = trainerRepository.findByUsernameIn(newTrainerUsernames);
        if (newTrainers.size() != newTrainerUsernames.size()) {
            throw new IllegalArgumentException("One or more trainers not found");
        }

        // Remove old trainers if they have no training with this trainee
        List<Trainer> trainersToRemove = new ArrayList<>(trainee.getAssignedTrainers());
        for (Trainer oldTrainer : trainersToRemove) {
            boolean hasTraining = trainee.getTrainings().stream()
                    .anyMatch(training -> training.getTrainer().equals(oldTrainer));
            if (!hasTraining) {
                trainee.getAssignedTrainers().remove(oldTrainer);
                //trainee.removeTrainer(oldTrainer);
            }
        }

        // Add new trainers
        for (Trainer newTrainer : newTrainers) {
            this.addTrainerToTrainee(trainee, newTrainer);
        }

        Trainee savedTrainee = traineeRepository.save(trainee);

        return savedTrainee.getAssignedTrainers().stream().map(trainerConverter::toTrainerBasicInfoDto).toList();
    }

    @Override
    public void addTrainerToTrainee(Trainee trainee, Trainer trainer) {
        if (!trainee.getAssignedTrainers().contains(trainer)) {
            trainee.getAssignedTrainers().add(trainer);
            traineeRepository.save(trainee);
        }
    }

    @Override
    public Trainee getActiveTraineeByUsername(String username) {
        log.info("Retrieving trainee with username {}", username);

        return traineeRepository.findActiveTraineeByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(Trainee.class.getSimpleName()));
    }

    @Override
    public List<TraineeProfileResponse> searchTrainees(UserSearchRequest searchRequest) {
        searchRequest.setUserType(UserType.TRAINEE);
        List<User> users = userService.searchUsers(searchRequest);
        return users.stream()
                .filter(user -> user instanceof Trainee)
                .map(user -> traineeConverter.toTraineeProfileResponse((Trainee) user))
                .toList();
    }

    private Trainee checkUserIsPresentAndTrainee(Optional<User> user) {
        if (user.isEmpty() || !(user.get() instanceof Trainee trainee)) {
            throw new EntityNotFoundException(Trainee.class.getSimpleName());
        }
        return trainee;
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

    private Trainee  buildTraineeFromRequest(TraineeCreatRequest createRequest, String rawPassword) {
        String username = getUsername(createRequest.firstName(), createRequest.lastName());

        Trainee trainee = new Trainee();
        trainee.setFirstName(createRequest.firstName());
        trainee.setLastName(createRequest.lastName());
        trainee.setUsername(username);
        trainee.setPassword((rawPassword));
        trainee.setDateOfBirth(createRequest.dateOfBirth());
        trainee.setAddress(createRequest.address());
        trainee.setUserType(UserType.TRAINEE);
        return trainee;
    }
}
