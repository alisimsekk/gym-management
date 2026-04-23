package com.alisimsek.repository;

import com.alisimsek.model.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    default Optional<Trainee> findActiveTraineeByUsername(String username) {
        return findByUsernameAndIsActive(username, true);
    }

    Optional<Trainee> findByUsernameAndIsActive(String username, boolean active);
}
