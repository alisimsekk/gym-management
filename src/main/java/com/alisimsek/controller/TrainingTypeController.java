package com.alisimsek.controller;

import com.alisimsek.dto.response.TrainingTypeResponse;
import com.alisimsek.service.TrainingTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/training-types")
@RequiredArgsConstructor
@Slf4j
public class TrainingTypeController {

    private final TrainingTypeService trainingTypeService;

    @GetMapping
    public ResponseEntity<List<TrainingTypeResponse>> getAllTrainingTypes() {
        return ResponseEntity.ok(trainingTypeService.getAllTrainingTypes());
    }
}