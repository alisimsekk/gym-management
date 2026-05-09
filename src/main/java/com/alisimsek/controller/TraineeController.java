package com.alisimsek.controller;

import com.alisimsek.dto.request.UpdateTraineeRequest;
import com.alisimsek.dto.request.UpdateTrainerListRequest;
import com.alisimsek.dto.request.UserSearchRequest;
import com.alisimsek.dto.response.TraineeProfileResponse;
import com.alisimsek.dto.response.TraineeUpdateResponse;
import com.alisimsek.dto.response.TrainerBasicInfoDto;
import com.alisimsek.service.TraineeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trainees")
@RequiredArgsConstructor
public class TraineeController {

    private final TraineeService traineeService;

    @Operation(summary = "Get trainee profile by username")
    @ApiResponse(responseCode = "200", description = "Trainee profile retrieved")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('TRAINEE') and #username == authentication.name)")
    @GetMapping("/{username}")
    public ResponseEntity<TraineeProfileResponse> getTraineeProfileByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(traineeService.getTraineeProfileByUsername(username));
    }

    @Operation(summary = "Get trainee profile by ID")
    @ApiResponse(responseCode = "200", description = "Trainee profile retrieved")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('TRAINEE') and #id == authentication.principal.id)")
    @GetMapping("/id/{id}")
    public ResponseEntity<TraineeProfileResponse> getTraineeById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(traineeService.getTraineeById(id));
    }

    @Operation(summary = "Update trainee profile")
    @ApiResponse(responseCode = "200", description = "Trainee profile updated")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('TRAINEE') and #id == authentication.principal.id)")
    @PutMapping("/{id}")
    public ResponseEntity<TraineeUpdateResponse> updateTraineeProfile(@PathVariable(value = "id") Long id, @Valid @RequestBody UpdateTraineeRequest request) {
        return ResponseEntity.ok(traineeService.updateTrainee(id, request));
    }

    @Operation(summary = "Delete trainee profile")
    @ApiResponse(responseCode = "200", description = "Trainee profile deleted")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTraineeProfile(@PathVariable(value = "username") String username) {
        traineeService.deleteTraineeByUsername(username);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update trainee active status")
    @ApiResponse(responseCode = "200", description = "Trainee status updated")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateTraineeStatus(@PathVariable(value = "id") Long id) {
        traineeService.changeTraineeStatus(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update Trainee's Trainer List")
    @ApiResponse(responseCode = "200", description = "Trainee's Trainer List updated")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('TRAINEE') and #username == authentication.name)")
    @PutMapping("/{username}/update-trainers")
    public ResponseEntity<List<TrainerBasicInfoDto>> updateTrainerList(
            @PathVariable(value = "username") String username,
            @Valid @RequestBody UpdateTrainerListRequest request) {
        return ResponseEntity.ok().body(traineeService.updateTrainerList(username, request));
    }

    @Operation(summary = "Search trainees")
    @ApiResponse(responseCode = "200", description = "Trainees retrieved")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('TRAINER'))")
    @PostMapping("/search")
    public ResponseEntity<List<TraineeProfileResponse>> searchTrainees(@RequestBody UserSearchRequest searchRequest) {
        return ResponseEntity.ok(traineeService.searchTrainees(searchRequest));
    }
}