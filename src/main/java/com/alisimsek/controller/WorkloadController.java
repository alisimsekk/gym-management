package com.alisimsek.controller;

import com.alisimsek.dto.response.TrainerWorkloadSummary;
import com.alisimsek.service.WorkloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class WorkloadController {

    private final WorkloadService workloadService;

    @GetMapping("/summary/{trainerUsername}")
    public ResponseEntity<TrainerWorkloadSummary> getWorkloadSummary(@PathVariable String trainerUsername) {
        return ResponseEntity.ok(workloadService.getWorkloadSummary(trainerUsername));
    }
}
