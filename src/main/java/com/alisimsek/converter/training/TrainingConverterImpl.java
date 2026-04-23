package com.alisimsek.converter.training;

import com.alisimsek.dto.response.TrainingResponse;
import com.alisimsek.model.Training;
import org.springframework.stereotype.Component;

@Component
public class TrainingConverterImpl implements TrainingConverter {

    @Override
    public TrainingResponse toTrainingResponse(Training training) {
        return TrainingResponse.builder()
                .id(training.getId())
                .trainerName(training.getTrainer().getFirstName().concat(" ").concat(training.getTrainer().getLastName()))
                .trainerUsername(training.getTrainer().getUsername())
                .traineeName(training.getTrainee().getFirstName().concat(" ").concat(training.getTrainee().getLastName()))
                .traineeUsername(training.getTrainee().getUsername())
                .trainingName(training.getTrainingName())
                .trainingDate(training.getTrainingDate())
                .trainingType(training.getTrainingType().getTrainingTypeName())
                .duration(training.getTrainingDuration())
                .build();
    }
}
