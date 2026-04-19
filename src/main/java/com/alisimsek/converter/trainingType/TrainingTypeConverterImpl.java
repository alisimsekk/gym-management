package com.alisimsek.converter.trainingType;

import com.alisimsek.dto.response.TrainingTypeResponse;
import com.alisimsek.model.TrainingType;
import org.springframework.stereotype.Component;

@Component
public class TrainingTypeConverterImpl implements TrainingTypeConverter {
    @Override
    public TrainingTypeResponse toTrainingTypeResponse(TrainingType trainingType) {
        return TrainingTypeResponse.builder()
                .id(trainingType.getId())
                .trainingTypeName(trainingType.getTrainingTypeName())
                .build();
    }
}
