package com.alisimsek.service;

import com.alisimsek.dto.request.TrainingTypeRequest;
import com.alisimsek.dto.request.TrainingTypeSearchRequest;
import com.alisimsek.dto.response.TrainingTypeResponse;
import com.alisimsek.model.TrainingType;

import java.util.List;

public interface TrainingTypeService {

    TrainingType getTrainingTypeById(Long id);

    List<TrainingTypeResponse> getAllTrainingTypes();

    TrainingTypeResponse createTrainingType(TrainingTypeRequest request);

    TrainingTypeResponse updateTrainingType(Long id, TrainingTypeRequest request);

    void deleteTrainingType(Long id);

    TrainingTypeResponse getTrainingTypeResponseById(Long id);

    List<TrainingTypeResponse> searchTrainingTypes(TrainingTypeSearchRequest searchRequest);
}
