package com.alisimsek.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UsernameGenerator {
    public String generateUsername(String username, Long size) {

        log.info("Generating username starting with {}", username);

        long prefixForUsername = size;
        String generatedUsername = username.concat(Long.toString(prefixForUsername));

        log.info("Generated username: " + generatedUsername);

        return generatedUsername;
    }
}
