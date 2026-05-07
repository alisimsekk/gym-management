package com.alisimsek.controller;

import com.alisimsek.dto.request.TrainingTypeRequest;
import com.alisimsek.dto.request.TrainingTypeSearchRequest;
import com.alisimsek.dto.response.TrainingTypeResponse;
import com.alisimsek.service.TrainingTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/training-types")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Training Type Management", description = "Endpoints for managing training types")
public class TrainingTypeController {

    private final TrainingTypeService trainingTypeService;

    @Operation(summary = "Get all training types", description = "Retrieves a list of all available training types")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    @GetMapping
    public ResponseEntity<List<TrainingTypeResponse>> getAllTrainingTypes() {
        return ResponseEntity.ok(trainingTypeService.getAllTrainingTypes());
    }

    @Operation(summary = "Get a training type by ID", description = "Retrieves specific training type details by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved training type"),
            @ApiResponse(responseCode = "404", description = "Training type not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TrainingTypeResponse> getTrainingTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(trainingTypeService.getTrainingTypeResponseById(id));
    }

    @Operation(summary = "Create a new training type", description = "Creates a new training type with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created training type"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<TrainingTypeResponse> createTrainingType(@Valid @RequestBody TrainingTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trainingTypeService.createTrainingType(request));
    }

    @Operation(summary = "Update an existing training type", description = "Updates the details of an existing training type by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated training type"),
            @ApiResponse(responseCode = "404", description = "Training type not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TrainingTypeResponse> updateTrainingType(@PathVariable Long id, @Valid @RequestBody TrainingTypeRequest request) {
        return ResponseEntity.ok(trainingTypeService.updateTrainingType(id, request));
    }

    @Operation(summary = "Delete a training type", description = "Deletes a specific training type by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted training type"),
            @ApiResponse(responseCode = "404", description = "Training type not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrainingType(@PathVariable Long id) {
        trainingTypeService.deleteTrainingType(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search training types", description = "Searches for training types based on the provided search criteria")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved search results")
    @PostMapping("/search")
    public ResponseEntity<List<TrainingTypeResponse>> searchTrainingTypes(@RequestBody TrainingTypeSearchRequest searchRequest) {
        return ResponseEntity.ok(trainingTypeService.searchTrainingTypes(searchRequest));
    }
}
