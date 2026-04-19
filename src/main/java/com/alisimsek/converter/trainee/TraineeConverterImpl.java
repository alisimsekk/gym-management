package com.alisimsek.converter.trainee;

import com.alisimsek.converter.trainer.TrainerConverter;
import com.alisimsek.dto.response.TraineeProfileResponse;
import com.alisimsek.dto.response.TraineeUpdateResponse;
import com.alisimsek.dto.response.UserRegistrationResponse;
import com.alisimsek.model.Trainee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TraineeConverterImpl implements TraineeConverter {

    private final TrainerConverter trainerConverter;

    @Override
    public UserRegistrationResponse toUserRegistrationResponse(Trainee trainee, String rawPassword) {
        return UserRegistrationResponse.builder()
                .username(trainee.getUsername())
                .password(rawPassword)
                .build();
    }

    @Override
    public TraineeProfileResponse toTraineeProfileResponse(Trainee trainee) {
        return TraineeProfileResponse.builder()
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .isActive(trainee.isActive())
                .trainers(!trainee.getAssignedTrainers().isEmpty() ? trainee.getAssignedTrainers().stream().map(trainerConverter::toTrainerBasicInfoDto).toList() : null)
                .build();
    }

    @Override
    public TraineeUpdateResponse toTraineeUpdateResponse(Trainee trainee) {
        return TraineeUpdateResponse.builder()
                .username(trainee.getUsername())
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .isActive(trainee.isActive())
                .trainers(!trainee.getAssignedTrainers().isEmpty() ? trainee.getAssignedTrainers().stream().map(trainerConverter::toTrainerBasicInfoDto).toList() : null)
                .build();
    }
}
