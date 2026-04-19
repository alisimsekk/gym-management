package com.alisimsek.controller;

import com.alisimsek.dto.request.TrainerCreateRequest;
import com.alisimsek.dto.request.UpdateTrainerRequest;
import com.alisimsek.dto.response.TrainerProfileResponse;
import com.alisimsek.dto.response.TrainerUpdateResponse;
import com.alisimsek.dto.response.UserRegistrationResponse;
import com.alisimsek.service.TrainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    @Operation(summary = "Register a new trainer")
    @ApiResponse(responseCode = "200", description = "Trainer successfully registered")
    @PostMapping
    public ResponseEntity<UserRegistrationResponse> registerTrainer(@Valid @RequestBody TrainerCreateRequest request) {
        return ResponseEntity.ok(trainerService.createTrainer(request));
    }

    @Operation(summary = "Get trainer profile")
    @ApiResponse(responseCode = "200", description = "Trainer profile retrieved")
    @GetMapping("/{username}")
    public ResponseEntity<TrainerProfileResponse> getTrainerProfileByUsername(@PathVariable(value = "username") String username) {
        return ResponseEntity.ok(trainerService.getTrainerProfileByUsername(username));
    }

    @Operation(summary = "Update trainer profile")
    @ApiResponse(responseCode = "200", description = "Trainer profile updated")
    @PutMapping("/{id}")
    public ResponseEntity<TrainerUpdateResponse> updateTrainerProfile(@PathVariable(value = "id") Long id, @Valid @RequestBody UpdateTrainerRequest request) {
        return ResponseEntity.ok(trainerService.updateTrainer(id, request));
    }

    @Operation(summary = "Update trainer active status")
    @ApiResponse(responseCode = "200", description = "Trainer status updated")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateTrainerStatus(@PathVariable(value = "id") Long id) {

        trainerService.changeTrainerStatus(id);
        return ResponseEntity.ok().build();
    }
}