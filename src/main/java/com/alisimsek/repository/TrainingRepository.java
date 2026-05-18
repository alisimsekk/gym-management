package com.alisimsek.repository;

import com.alisimsek.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long>, JpaSpecificationExecutor<Training> {

    @Query("""
            SELECT t.trainingDateTime FROM Training t
            WHERE t.trainer.id = :trainerId
              AND t.trainingDateTime >= :dayStart
              AND t.trainingDateTime <= :dayEnd
            """)
    List<LocalDateTime> findTrainingDateTimesByTrainerIdAndDay(
            @Param("trainerId") Long trainerId,
            @Param("dayStart") LocalDateTime dayStart,
            @Param("dayEnd") LocalDateTime dayEnd);

    @Query("""
            SELECT t.trainingDateTime FROM Training t
            WHERE t.trainee.id = :traineeId
              AND t.trainingDateTime >= :dayStart
              AND t.trainingDateTime <= :dayEnd
            """)
    List<LocalDateTime> findTrainingDateTimesByTraineeIdAndDay(
            @Param("traineeId") Long traineeId,
            @Param("dayStart") LocalDateTime dayStart,
            @Param("dayEnd") LocalDateTime dayEnd);

    boolean existsByTrainerIdAndTrainingDateTime(Long trainerId, LocalDateTime trainingDateTime);

    boolean existsByTraineeIdAndTrainingDateTime(Long traineeId, LocalDateTime trainingDateTime);

    boolean existsByTrainerIdAndTrainingDateTimeAndIdNot(Long trainerId, LocalDateTime trainingDateTime, Long id);

    boolean existsByTraineeIdAndTrainingDateTimeAndIdNot(Long traineeId, LocalDateTime trainingDateTime, Long id);

    Optional<Training> findByIdAndTraineeId(Long id, Long traineeId);
}
