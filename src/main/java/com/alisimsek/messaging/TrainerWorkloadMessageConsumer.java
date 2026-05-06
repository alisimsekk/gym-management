package com.alisimsek.messaging;

import com.alisimsek.dto.request.TrainerWorkloadRequest;
import com.alisimsek.service.WorkloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainerWorkloadMessageConsumer {

    private final WorkloadService workloadService;

    @JmsListener(destination = "${spring.jms.template.default-destination:trainer-workload-queue}")
    public void handleTrainerWorkloadMessage(TrainerWorkloadMessage message) {
        try {
            log.info("Received trainer workload message: {} for trainer: {}", 
                    message.getMessageId(), message.getTrainerUsername());
            
            // Validate message
            if (!isValidMessage(message)) {
                log.error("Invalid trainer workload message: {}", message.getMessageId());
                throw new IllegalArgumentException("Invalid message - required fields missing");
            }
            
            // Map message to DTO and process via service API
            TrainerWorkloadRequest request = buildTrainerWorkloadRequest(message);

            workloadService.handleWorkload(request);
            
            log.info("Successfully processed trainer workload message: {} for trainer: {}", 
                    message.getMessageId(), message.getTrainerUsername());
            
        } catch (Exception e) {
            log.error("Error processing trainer workload message: {} for trainer: {}. Error: {}", 
                    message.getMessageId(), message.getTrainerUsername(), e.getMessage(), e);
            throw e; // Re-throw to trigger redelivery and eventually move to DLQ
        }
    }

    @JmsListener(destination = "ActiveMQ.DLQ")
    public void handleDeadLetterMessage(TrainerWorkloadMessage message) {
        try {
            log.warn("Received message in Dead Letter Queue: {}", message);

            // Validate message
            if (!isValidMessage(message)) {
                log.error("Invalid trainer workload message: {}", message.getMessageId());
                throw new IllegalArgumentException("Invalid message - required fields missing");
            }

            // Map message to DTO and process via service API
            TrainerWorkloadRequest request = buildTrainerWorkloadRequest(message);

            workloadService.handleWorkload(request);

        } catch (Exception e) {
            log.error("Error processing dead letter message: {}", e.getMessage(), e);
        }
    }

    private boolean isValidMessage(TrainerWorkloadMessage message) {
        if (message == null) {
            return false;
        }
        
        if (message.getTrainerUsername() == null || message.getTrainerUsername().trim().isEmpty()) {
            log.error("Invalid message - trainerUsername is null or empty");
            return false;
        }
        
        if (message.getTrainerFirstName() == null || message.getTrainerFirstName().trim().isEmpty()) {
            log.error("Invalid message - trainerFirstName is null or empty");
            return false;
        }
        
        if (message.getTrainerLastName() == null || message.getTrainerLastName().trim().isEmpty()) {
            log.error("Invalid message - trainerLastName is null or empty");
            return false;
        }
        
        if (message.getTrainingDate() == null) {
            log.error("Invalid message - trainingDate is null");
            return false;
        }
        
        if (message.getTrainingDuration() == null || message.getTrainingDuration() <= 0) {
            log.error("Invalid message - trainingDuration is null or invalid");
            return false;
        }
        
        if (message.getActionType() == null) {
            log.error("Invalid message - actionType is null");
            return false;
        }
        
        return true;
    }

    private static TrainerWorkloadRequest buildTrainerWorkloadRequest(TrainerWorkloadMessage message) {
        return TrainerWorkloadRequest.builder()
                .trainerUsername(message.getTrainerUsername())
                .trainerFirstName(message.getTrainerFirstName())
                .trainerLastName(message.getTrainerLastName())
                .active(message.isActive())
                .trainingDate(message.getTrainingDate())
                .trainingDuration(message.getTrainingDuration())
                .actionType(message.getActionType())
                .build();
    }
}
