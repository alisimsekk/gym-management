package com.alisimsek.converter.training;

import com.alisimsek.dto.response.TrainingResponse;
import com.alisimsek.model.Training;

public interface TrainingConverter {
    TrainingResponse toTrainingResponse(Training training);
}
