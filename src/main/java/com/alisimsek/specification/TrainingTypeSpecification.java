package com.alisimsek.specification;

import com.alisimsek.dto.request.TrainingTypeSearchRequest;
import com.alisimsek.model.TrainingType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TrainingTypeSpecification {

    public static Specification<TrainingType> searchTrainingTypes(TrainingTypeSearchRequest searchRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (searchRequest.getTrainingName() != null && !searchRequest.getTrainingName().isBlank()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("trainingTypeName")), "%" + searchRequest.getTrainingName().toLowerCase() + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
