package com.alisimsek.service.impl;

import com.alisimsek.converter.trainingType.TrainingTypeConverter;
import com.alisimsek.dto.request.TrainingTypeRequest;
import com.alisimsek.dto.request.TrainingTypeSearchRequest;
import com.alisimsek.dto.response.TrainingTypeResponse;
import com.alisimsek.exception.customException.EntityNotFoundException;
import com.alisimsek.model.TrainingType;
import com.alisimsek.repository.TrainingTypeRepository;
import com.alisimsek.service.TrainingTypeService;
import com.alisimsek.specification.TrainingTypeSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
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
        return trainingTypeRepository.findAll().stream()
                .map(trainingTypeConverter::toTrainingTypeResponse)
                .toList();
    }

    @Override
    public TrainingTypeResponse createTrainingType(TrainingTypeRequest request) {
        log.info("Creating training type with name {}", request.getTrainingName());
        TrainingType trainingType = trainingTypeConverter.toTrainingType(request);
        TrainingType savedTrainingType = trainingTypeRepository.save(trainingType);
        return trainingTypeConverter.toTrainingTypeResponse(savedTrainingType);
    }

    @Override
    public TrainingTypeResponse updateTrainingType(Long id, TrainingTypeRequest request) {
        log.info("Updating training type with id {}", id);
        TrainingType existingTrainingType = getTrainingTypeById(id);
        existingTrainingType.setTrainingTypeName(request.getTrainingName());
        TrainingType updatedTrainingType = trainingTypeRepository.save(existingTrainingType);
        return trainingTypeConverter.toTrainingTypeResponse(updatedTrainingType);
    }

    @Override
    public void deleteTrainingType(Long id) {
        log.info("Deleting training type with id {}", id);
        TrainingType existingTrainingType = getTrainingTypeById(id);
        trainingTypeRepository.delete(existingTrainingType);
    }

    @Override
    public TrainingTypeResponse getTrainingTypeResponseById(Long id) {
        log.info("Retrieving training type response with id {}", id);
        return trainingTypeConverter.toTrainingTypeResponse(getTrainingTypeById(id));
    }

    @Override
    public List<TrainingTypeResponse> searchTrainingTypes(TrainingTypeSearchRequest searchRequest) {
        log.info("Searching training types with parameters: {}", searchRequest);
        Specification<TrainingType> spec = TrainingTypeSpecification.searchTrainingTypes(searchRequest);
        return trainingTypeRepository.findAll(spec).stream()
                .map(trainingTypeConverter::toTrainingTypeResponse)
                .toList();
    }
}
