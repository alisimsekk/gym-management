package com.alisimsek.controller;

import com.alisimsek.dto.request.TrainingRequest;
import com.alisimsek.dto.request.TrainingSearchRequest;
import com.alisimsek.dto.request.UpdateTrainingRequest;
import com.alisimsek.dto.response.AvailableTrainingSlotsResponse;
import com.alisimsek.dto.response.TrainingResponse;
import com.alisimsek.service.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/trainings")
@RequiredArgsConstructor
@Validated
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

    @Operation(summary = "Get available training time slots for a date")
    @ApiResponse(responseCode = "200", description = "Available slots retrieved")
    @GetMapping("/available-slots")
    public ResponseEntity<AvailableTrainingSlotsResponse> getAvailableTrainingSlots(
            @RequestParam @NotBlank String trainerUsername,
            @RequestParam @NotBlank String traineeUsername,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long excludeTrainingId) {
        return ResponseEntity.ok(
                trainingService.getAvailableTrainingSlots(
                        trainerUsername, traineeUsername, date, excludeTrainingId));
    }

    @Operation(summary = "Get training by ID")
    @ApiResponse(responseCode = "200", description = "Training successfully retrieved")
    @GetMapping("/{id}")
    public ResponseEntity<TrainingResponse> getTrainingById(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok().body(trainingService.getTrainingById(id));
    }

    @Operation(summary = "Get all trainings")
    @ApiResponse(responseCode = "200", description = "Trainings successfully retrieved")
    @PreAuthorize("hasRole('ADMIN')")
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
