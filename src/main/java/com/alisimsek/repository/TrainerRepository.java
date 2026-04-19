package com.alisimsek.repository;

import com.alisimsek.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    default Optional<Trainer> findActiveTrainerByUsername(String username) {
        return findByUsernameAndIsActive(username, true);
    }

    Optional<Trainer> findByUsernameAndIsActive(String username, boolean active);

    List<Trainer> findByUsernameIn(List<String> usernames);
}