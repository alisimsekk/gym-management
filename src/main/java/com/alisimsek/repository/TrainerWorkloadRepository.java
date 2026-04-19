package com.alisimsek.repository;

import com.alisimsek.model.TrainerWorkload;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TrainerWorkloadRepository extends MongoRepository<TrainerWorkload, String> {
}
