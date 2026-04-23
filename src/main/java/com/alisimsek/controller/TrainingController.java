package com.alisimsek.controller;

import com.alisimsek.dto.request.TrainingRequest;
import com.alisimsek.dto.request.TrainingSearchRequest;
import com.alisimsek.dto.request.UpdateTrainingRequest;
import com.alisimsek.dto.response.TrainingResponse;
import com.alisimsek.service.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trainings")
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingService trainingService;

    @Operation(summary = "Add a new training")
    @ApiResponse(responseCode = "200", description = "Training successfully added")
    @PostMapping
    public ResponseEntity<Void> createTraining(@Valid @RequestBody TrainingRequest request) {
        trainingService.createTraining(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update training")
    @ApiResponse(responseCode = "200", description = "Training successfully updated")
    @PutMapping("/{id}")
    public ResponseEntity<TrainingResponse> updateTraining(@PathVariable(value = "id") Long id, @Valid @RequestBody UpdateTrainingRequest updateTrainingRequest) {
        return ResponseEntity.ok().body(trainingService.updateTraining(id, updateTrainingRequest));
    }

    @Operation(summary = "Get training by ID")
    @ApiResponse(responseCode = "200", description = "Training successfully retrieved")
    @GetMapping("/{id}")
    public ResponseEntity<TrainingResponse> getTrainingById(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok().body(trainingService.getTrainingById(id));
    }

    @Operation(summary = "Get all trainings")
    @ApiResponse(responseCode = "200", description = "Trainings successfully retrieved")
    @GetMapping
    public ResponseEntity<List<TrainingResponse>> getAllTrainings() {
        return ResponseEntity.ok().body(trainingService.getAllTrainings());
    }

    @Operation(summary = "Delete training")
    @ApiResponse(responseCode = "200", description = "Training successfully deleted")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTraining(@PathVariable(value = "id") Long id) {
        trainingService.deleteTraining(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Search trainings")
    @ApiResponse(responseCode = "200", description = "Trainings successfully retrieved")
    @PostMapping("/search")
    public ResponseEntity<List<TrainingResponse>> searchTraining(@RequestBody TrainingSearchRequest trainingSearchRequest) {
        return ResponseEntity.ok().body(trainingService.searchTraining(trainingSearchRequest));
    }
}
