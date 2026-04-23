package com.alisimsek.service;

import com.alisimsek.dto.request.TrainingRequest;
import com.alisimsek.dto.request.TrainingSearchRequest;
import com.alisimsek.dto.request.UpdateTrainingRequest;
import com.alisimsek.dto.response.TrainingResponse;

import java.util.List;

public interface TrainingService {

    void createTraining(TrainingRequest createRequest);

    TrainingResponse updateTraining(Long id, UpdateTrainingRequest updateTrainingRequest);

    TrainingResponse getTrainingById(Long id);

    List<TrainingResponse> getAllTrainings();

    void deleteTraining(Long id);

    List<TrainingResponse> searchTraining(TrainingSearchRequest trainingSearchRequest);
}
