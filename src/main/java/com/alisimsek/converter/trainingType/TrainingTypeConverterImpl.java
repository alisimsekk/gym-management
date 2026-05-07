package com.alisimsek.converter.trainingType;

import com.alisimsek.dto.request.TrainingTypeRequest;
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

    @Override
    public TrainingType toTrainingType(TrainingTypeRequest request) {
        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName(request.getTrainingName());
        return trainingType;
    }
}
