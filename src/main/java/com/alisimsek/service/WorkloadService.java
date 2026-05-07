package com.alisimsek.service;

import com.alisimsek.dto.request.TrainerWorkloadRequest;
import com.alisimsek.dto.response.TrainerWorkloadSummary;
import com.alisimsek.model.TrainerWorkload;

import java.util.Optional;

public interface WorkloadService {
    void handleWorkload(TrainerWorkloadRequest trainerWorkloadRequest);
    TrainerWorkloadSummary getWorkloadSummary(String trainerUsername);
    Optional<TrainerWorkload> getTrainerWorkloadByUsername(String trainerUsername);
    void updateWorkload(TrainerWorkload trainerWorkload);
}
