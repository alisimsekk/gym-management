package com.alisimsek.exception;

import java.util.HashMap;
import java.util.Map;

public class ErrorCodeLookUp {

    private ErrorCodeLookUp() {}

    private static final Map<String, String> value;

    static {
        value = new HashMap<>();
        value.put("4000", "ENTITY_NOT_FOUND_EXCEPTION");
        value.put("4001", "ENTITY_ALREADY_EXISTS_EXCEPTION");
        value.put("4002", "TRAINER_WORKLOAD_NOT_FOUND_EXCEPTION");
        value.put("4003", "PASSWORD_MISMATCH_EXCEPTION");
        value.put("4004", "USER_MISMATCH_EXCEPTION");
        value.put("4005", "USER_LOCKED_EXCEPTION");
        value.put("4006", "INVALID_TRAINING_TIME_SLOT_EXCEPTION");
        value.put("4007", "TRAINING_DURATION_EXCEEDED_EXCEPTION");
        value.put("4008", "TRAINING_PAST_DATETIME_EXCEPTION");
        value.put("4009", "TRAINER_SCHEDULE_CONFLICT_EXCEPTION");
        value.put("4010", "TRAINEE_SCHEDULE_CONFLICT_EXCEPTION");
    }

    public static String getMessageKey(String errorCode) {
        return value.getOrDefault(errorCode, "GENERIC_EXCEPTION");
    }
}
