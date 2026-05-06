package com.alisimsek.service;

import com.alisimsek.dto.request.TrainerWorkloadRequest;
import com.alisimsek.dto.response.TrainerWorkloadSummary;

public interface WorkloadService {
    void handleWorkload(TrainerWorkloadRequest trainerWorkloadRequest);
    TrainerWorkloadSummary getWorkloadSummary(String trainerUsername);
}
