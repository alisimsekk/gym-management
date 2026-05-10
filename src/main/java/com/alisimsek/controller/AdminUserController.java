package com.alisimsek.controller;

import com.alisimsek.dto.request.AdminCreateRequest;
import com.alisimsek.dto.request.TraineeCreatRequest;
import com.alisimsek.dto.request.TrainerCreateRequest;
import com.alisimsek.dto.request.UpdateAdminProfileRequest;
import com.alisimsek.dto.response.AdminProfileResponse;
import com.alisimsek.dto.response.UserRegistrationResponse;
import com.alisimsek.service.AdminService;
import com.alisimsek.service.TraineeService;
import com.alisimsek.service.TrainerService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final AdminService adminService;

    @Operation(summary = "Create a new Trainee with admin privileges")
    @PostMapping("/trainee")
    public ResponseEntity<UserRegistrationResponse> createTrainee(@Valid @RequestBody TraineeCreatRequest request) {
        return ResponseEntity.ok(traineeService.createTrainee(request));
    }

    @Operation(summary = "Create a new Trainer with admin privileges")
    @PostMapping("/trainer")
    public ResponseEntity<UserRegistrationResponse> createTrainer(@Valid @RequestBody TrainerCreateRequest request) {
        return ResponseEntity.ok(trainerService.createTrainer(request));
    }

    @Operation(summary = "Create a new Admin with admin privileges")
    @PostMapping("/admin")
    public ResponseEntity<UserRegistrationResponse> createAdmin(@Valid @RequestBody AdminCreateRequest request) {
        return ResponseEntity.ok(adminService.createAdmin(request));
    }

    @Operation(summary = "Get current admin profile (self)")
    @GetMapping("/me")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminProfileResponse> getCurrentAdminProfile(Authentication authentication) {
        return ResponseEntity.ok(adminService.getMyAdminProfile(authentication.getName()));
    }

    @Operation(summary = "Update current admin profile (self)")
    @PutMapping("/me")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminProfileResponse> updateCurrentAdminProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateAdminProfileRequest request
    ) {
        return ResponseEntity.ok(adminService.updateMyAdminProfile(authentication.getName(), request));
    }
}