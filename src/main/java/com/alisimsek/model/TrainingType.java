package com.alisimsek.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TrainingType extends BaseEntity {

    private String trainingTypeName;

    @Override
    public String toString() {
        return "TrainingType{" +
                "id=" + getId() + '\'' +
                "trainingTypeName='" + trainingTypeName + '\'' +
                '}';
    }
}
