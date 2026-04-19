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

    @Field("years")
    private List<YearWorkload> years = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class YearWorkload {
        private int year;
        private List<MonthWorkload> months = new ArrayList<>();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthWorkload {
        private int month; // 1-12
        private int trainingSummaryDuration; // total duration for the month
    }
}
