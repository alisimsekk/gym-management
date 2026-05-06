package com.alisimsek.messaging;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseMessage {
    private String messageId;
    private String correlationId;
    private String sourceService;
    private String destinationService;
    private String messageType;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime timestamp;
    private int retryCount;

    public static BaseMessage create(String sourceService, String destinationService, String messageType) {
        return BaseMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .correlationId(UUID.randomUUID().toString())
                .sourceService(sourceService)
                .destinationService(destinationService)
                .messageType(messageType)
                .timestamp(LocalDateTime.now())
                .retryCount(0)
                .build();
    }
}

