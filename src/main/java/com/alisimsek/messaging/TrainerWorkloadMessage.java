package com.alisimsek.messaging;

import com.alisimsek.enums.ActionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TrainerWorkloadMessage extends BaseMessage {
    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private boolean active;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate trainingDate;
    private Integer trainingDuration;
    private ActionType actionType;

    private static final String SOURCE_SERVICE = "gym-management";
    private static final String DESTINATION_SERVICE = "gym-management";
    private static final String MESSAGE_TYPE = "TRAINER_WORKLOAD";

    public static TrainerWorkloadMessage create(String trainerUsername, String trainerFirstName, 
                                              String trainerLastName, boolean active, 
                                              LocalDate trainingDate, Integer trainingDuration, 
                                              ActionType actionType) {
        BaseMessage baseMessage = BaseMessage.create(SOURCE_SERVICE, DESTINATION_SERVICE, MESSAGE_TYPE);

        
        return TrainerWorkloadMessage.builder()
                .messageId(baseMessage.getMessageId())
                .correlationId(baseMessage.getCorrelationId())
                .sourceService(baseMessage.getSourceService())
                .destinationService(baseMessage.getDestinationService())
                .messageType(baseMessage.getMessageType())
                .timestamp(baseMessage.getTimestamp())
                .retryCount(baseMessage.getRetryCount())
                .trainerUsername(trainerUsername)
                .trainerFirstName(trainerFirstName)
                .trainerLastName(trainerLastName)
                .active(active)
                .trainingDate(trainingDate)
                .trainingDuration(trainingDuration)
                .actionType(actionType)
                .build();
    }
}

