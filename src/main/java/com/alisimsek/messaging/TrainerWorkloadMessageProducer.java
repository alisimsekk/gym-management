package com.alisimsek.messaging;

import com.alisimsek.dto.request.TrainerWorkloadRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainerWorkloadMessageProducer {

    private final JmsTemplate jmsTemplate;

    @Value("${spring.jms.template.default-destination}")
    private String defaultDestination;

    public void publishTrainerWorkload(TrainerWorkloadRequest workloadRequest) {

        try {
            TrainerWorkloadMessage message = TrainerWorkloadMessage.create(
                    workloadRequest.getTrainerUsername(), workloadRequest.getTrainerFirstName(), workloadRequest.getTrainerLastName(),
                    workloadRequest.isActive(), workloadRequest.getTrainingDate(), workloadRequest.getTrainingDuration(),
                    workloadRequest.getActionType()
            );

            log.info("Publishing trainer workload message: {}", message);
            
            jmsTemplate.convertAndSend(defaultDestination, message);
            
            log.info("Successfully published trainer workload message with ID: {}", message.getMessageId());
        } catch (Exception e) {
            log.error("Failed to publish trainer workload message for trainer: {}. Error: {}",
                    workloadRequest.getTrainerUsername(), e.getMessage(), e);
            throw new RuntimeException("Failed to publish trainer workload message", e);
        }
    }

    public void publishTrainerWorkloadList(java.util.List<TrainerWorkloadRequest> workloadList) {
        try {
            log.info("Publishing trainer workload list with {} items", workloadList.size());
            
            for (TrainerWorkloadRequest request : workloadList) {
                TrainerWorkloadMessage message = TrainerWorkloadMessage.create(
                    request.getTrainerUsername(),
                    request.getTrainerFirstName(),
                    request.getTrainerLastName(),
                    request.isActive(),
                    request.getTrainingDate(),
                    request.getTrainingDuration(),
                    request.getActionType()
                );
                
                jmsTemplate.convertAndSend(defaultDestination, message);
            }
            
            log.info("Successfully published {} trainer workload messages", workloadList.size());
        } catch (Exception e) {
            log.error("Failed to publish trainer workload list. Error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to publish trainer workload list", e);
        }
    }
}

