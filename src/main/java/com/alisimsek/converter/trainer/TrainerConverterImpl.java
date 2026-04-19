package com.alisimsek.converter.trainer;

import com.alisimsek.dto.response.*;
import com.alisimsek.model.Trainee;
import com.alisimsek.model.Trainer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class TrainerConverterImpl implements TrainerConverter {

    public UserRegistrationResponse toUserRegistrationResponse(Trainer trainer, String rawPassword) {
        return UserRegistrationResponse.builder()
                .username(trainer.getUsername())
                .password(rawPassword)
                .build();
    }

    public TrainerBasicInfoDto toTrainerBasicInfoDto(Trainer trainer) {
        return TrainerBasicInfoDto.builder()
                .username(trainer.getUsername())
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .specialization(Objects.nonNull(trainer.getSpecialization()) ? trainer.getSpecialization().getTrainingTypeName() : null)
                .build();
    }

    public TrainerProfileResponse toTrainerProfileResponse(Trainer trainer) {
        return TrainerProfileResponse.builder()
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .specialization(Objects.nonNull(trainer.getSpecialization()) ? trainer.getSpecialization().getTrainingTypeName() : null)
                .isActive(trainer.isActive())
                .trainees(!trainer.getTrainees().isEmpty() ? trainer.getTrainees().stream().map(this::toTraineeBasicInfoDto).toList() : null)
                .build();
    }

    @Override
    public TrainerUpdateResponse toTrainerUpdateResponse(Trainer trainer) {
        return TrainerUpdateResponse.builder()
                .username(trainer.getUsername())
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .specialization(Objects.nonNull(trainer.getSpecialization()) ? trainer.getSpecialization().getTrainingTypeName() : null)
                .isActive(trainer.isActive())
                .trainees(!trainer.getTrainees().isEmpty() ? trainer.getTrainees().stream().map(this::toTraineeBasicInfoDto).toList() : null)
                .build();
    }

    private TraineeBasicInfoDto toTraineeBasicInfoDto(Trainee trainee) {
        return TraineeBasicInfoDto.builder()
                .username(trainee.getUsername())
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .build();
    }

}
