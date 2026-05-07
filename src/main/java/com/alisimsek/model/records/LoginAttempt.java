package com.alisimsek.model.records;

import java.time.LocalDateTime;

public record LoginAttempt(int count, LocalDateTime blockedUntil) {
}
