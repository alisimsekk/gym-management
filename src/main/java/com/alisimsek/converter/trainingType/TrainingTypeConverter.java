package com.alisimsek.converter.trainingType;

import com.alisimsek.dto.request.TrainingTypeRequest;
import com.alisimsek.dto.response.TrainingTypeResponse;
import com.alisimsek.model.TrainingType;

public interface TrainingTypeConverter {
    TrainingTypeResponse toTrainingTypeResponse(TrainingType trainingType);
    TrainingType toTrainingType(TrainingTypeRequest request);
}
