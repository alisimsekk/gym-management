package com.alisimsek.specification;

import com.alisimsek.dto.request.TrainingSearchRequest;
import com.alisimsek.model.Training;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import static com.alisimsek.constant.AppConstants.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TrainingSpecification {

    public static Specification<Training> search(TrainingSearchRequest searchRequest) {
        return (root, query, cb) -> {
            var predicates = new java.util.ArrayList<Predicate>();

            if (StringUtils.hasText(searchRequest.getTraineeUsername())) {
                predicates.add(cb.equal(root.get(TRAINEE).get(USERNAME), searchRequest.getTraineeUsername()));
            }

            if (StringUtils.hasText(searchRequest.getTrainerUsername())) {
                predicates.add(cb.equal(root.get(TRAINER).get(USERNAME), searchRequest.getTrainerUsername()));
            }

            if (StringUtils.hasText(searchRequest.getTraineeName())) {
                predicates.add(cb.like(cb.lower(root.get(TRAINEE).get("firstName")),
                        "%" + searchRequest.getTraineeName().toLowerCase() + "%"));
            }

            if (StringUtils.hasText(searchRequest.getTrainerName())) {
                predicates.add(cb.like(cb.lower(root.get(TRAINER).get("firstName")),
                        "%" + searchRequest.getTrainerName().toLowerCase() + "%"));
            }

            if (StringUtils.hasText(searchRequest.getTrainingTypeName())) {
                predicates.add(cb.like(cb.lower(root.get(TRAINING_TYPE).get("trainingTypeName")),
                        "%" + searchRequest.getTrainingTypeName().toLowerCase() + "%"));
            }

            if (searchRequest.getPeriodFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(TRAINING_DATE), searchRequest.getPeriodFrom()));
            }

            if (searchRequest.getPeriodTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(TRAINING_DATE), searchRequest.getPeriodTo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
