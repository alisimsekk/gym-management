package com.alisimsek.service;

import com.alisimsek.dto.request.TrainerCreateRequest;
import com.alisimsek.dto.request.UpdateTrainerRequest;
import com.alisimsek.dto.request.UserSearchRequest;
import com.alisimsek.dto.response.TrainerProfileResponse;
import com.alisimsek.dto.response.TrainerUpdateResponse;
import com.alisimsek.dto.response.UserRegistrationResponse;
import com.alisimsek.model.Trainer;

import java.util.List;

public interface TrainerService {

    UserRegistrationResponse createTrainer(TrainerCreateRequest request);

    TrainerUpdateResponse updateTrainer(Long id, UpdateTrainerRequest trainer);

    TrainerProfileResponse getTrainerById(Long id);

    TrainerProfileResponse getTrainerProfileByUsername(String username);

    Trainer getActiveTrainerByUsername(String username);

    void changeTrainerStatus(Long id);

    List<TrainerProfileResponse> searchTrainers(UserSearchRequest searchRequest);
}
