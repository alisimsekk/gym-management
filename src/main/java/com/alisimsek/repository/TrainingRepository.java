package com.alisimsek.repository;

import com.alisimsek.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long>, JpaSpecificationExecutor<Training> {

    Optional<Training> findByTrainerIdAndTraineeIdAndTrainingTypeIdAndTrainingDate(
            Long trainerId,
            Long traineeId,
            Long trainingTypeId,
            LocalDate trainingDate
    );

    Optional<Training> findByIdAndTraineeId(Long id, Long traineeId);
}
