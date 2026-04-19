package com.alisimsek.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Trainer extends User {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "specialization_id", nullable = false)
    private TrainingType specialization;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Training> trainings;

    @ManyToMany(mappedBy = "assignedTrainers")
    private List<Trainee> trainees = new ArrayList<>();

    @Override
    public String toString() {
        return "Trainer{" +
                "id=" + getId() + '\'' +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", username='" + getUsername() + '\'' +
                ", password='" + "*****" + '\'' +
                ", specialization=" + specialization + '\'' +
                ", isActive=" + isActive() +
                '}';
    }
}
