package com.alisimsek.service;

import com.alisimsek.dto.request.TrainerCreateRequest;
import com.alisimsek.dto.request.UpdateTrainerRequest;
import com.alisimsek.dto.response.TrainerProfileResponse;
import com.alisimsek.dto.response.TrainerUpdateResponse;
import com.alisimsek.dto.response.UserRegistrationResponse;
import com.alisimsek.model.Trainer;

public interface TrainerService {

    UserRegistrationResponse createTrainer(TrainerCreateRequest request);

    TrainerUpdateResponse updateTrainer(Long id, UpdateTrainerRequest trainer);

    Trainer getrainerById(Long id);

    TrainerProfileResponse getTrainerProfileByUsername(String username);

    Trainer getActiveTrainerByUsername(String username);

    void changeTrainerStatus(Long id);
}