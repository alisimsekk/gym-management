package com.alisimsek.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.request-logging")
public class RequestLoggingProperties {

    private boolean enabled = true;

    private List<String> excludePaths = new ArrayList<>(List.of(
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/actuator/**"
    ));
}
