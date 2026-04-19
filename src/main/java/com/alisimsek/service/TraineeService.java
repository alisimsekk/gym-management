package com.alisimsek.service;

import com.alisimsek.dto.request.*;
import com.alisimsek.dto.response.TraineeProfileResponse;
import com.alisimsek.dto.response.TraineeUpdateResponse;
import com.alisimsek.dto.response.TrainerBasicInfoDto;
import com.alisimsek.dto.response.UserRegistrationResponse;
import com.alisimsek.model.Trainee;
import com.alisimsek.model.Trainer;

import java.util.List;

public interface TraineeService {

    UserRegistrationResponse createTrainee(TraineeCreatRequest createRequest);

    TraineeUpdateResponse updateTrainee(Long id, UpdateTraineeRequest trainee);

    Trainee getTraineeById(Long id);

    void deleteTraineeByUsername(String username);

    TraineeProfileResponse getTraineeProfileByUsername(String username);

    void changeTraineeStatus(Long userId);

    List<TrainerBasicInfoDto> updateTrainerList(String username, UpdateTrainerListRequest request);

    void addTrainerToTrainee(Trainee trainee, Trainer trainer);
}