package com.alisimsek.service.impl;

import com.alisimsek.converter.trainingType.TrainingTypeConverter;
import com.alisimsek.dto.response.TrainingTypeResponse;
import com.alisimsek.exception.customException.EntityNotFoundException;
import com.alisimsek.model.TrainingType;
import com.alisimsek.repository.TrainingTypeRepository;
import com.alisimsek.service.TrainingTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingTypeConverter trainingTypeConverter;

    @Override
    public TrainingType getTrainingTypeById(Long id) {
        log.info("Retrieving training type with id {}", id);

        return trainingTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TrainingType.class.getSimpleName()));
    }

    @Override
    public List<TrainingTypeResponse> getAllTrainingTypes() {
        log.info("Retrieving all TrainingTypes");

        return trainingTypeRepository.findAll().stream().map(trainingTypeConverter::toTrainingTypeResponse).toList();
    }
}
