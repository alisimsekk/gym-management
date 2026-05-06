package com.alisimsek.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "trainer_workload")
@CompoundIndexes({
        @CompoundIndex(name = "trainer_name_idx", def = "{ 'trainerFirstName': 1, 'trainerLastName': 1 }")
})
public class TrainerWorkload {
    @Id
    private String id;

    @Indexed
    private String trainerUsername;

    @Indexed
    private String trainerFirstName;

    @Indexed
    private String trainerLastName;

    private boolean active;

    @Field("yearlyWorkloads")
    private List<YearlyWorkload> yearlyWorkloads = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class YearlyWorkload {
        private int year;
        private List<MonthlyWorkload> monthlyWorkloads = new ArrayList<>();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlyWorkload {
        private int month; // 1-12
        private int totalTrainingDuration; // total duration for the month
    }
}
