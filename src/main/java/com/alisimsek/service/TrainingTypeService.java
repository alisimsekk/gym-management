package com.alisimsek.service;

import com.alisimsek.dto.response.TrainingTypeResponse;
import com.alisimsek.model.TrainingType;

import java.util.List;

public interface TrainingTypeService {

    TrainingType getTrainingTypeById(Long id);

    List<TrainingTypeResponse> getAllTrainingTypes();
}
