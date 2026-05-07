package com.alisimsek.controller;

import com.alisimsek.dto.request.ChangePasswordRequest;
import com.alisimsek.dto.request.LoginRequest;
import com.alisimsek.dto.request.TraineeCreatRequest;
import com.alisimsek.dto.response.AuthResponse;
import com.alisimsek.dto.response.UserRegistrationResponse;
import com.alisimsek.service.AuthService;
import com.alisimsek.service.TraineeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TraineeService traineeService;

    @Operation(summary = "Register a new trainee")
    @ApiResponse(responseCode = "200", description = "Trainee successfully registered")
    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponse> registerTrainee(@Valid @RequestBody TraineeCreatRequest request) {
        return ResponseEntity.ok(traineeService.createTrainee(request));
    }

    @Operation(summary = "Login with username and password")
    @ApiResponse(responseCode = "200", description = "Successfully logged in")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok((authService.authenticateUser(loginRequest)));
    }

    @Operation(summary = "Change user password")
    @ApiResponse(responseCode = "200", description = "Password successfully changed")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINER') or hasRole('TRAINEE')")
    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok().build();
    }
}