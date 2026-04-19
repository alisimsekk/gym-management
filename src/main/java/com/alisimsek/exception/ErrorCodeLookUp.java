package com.alisimsek.exception;

import java.util.HashMap;
import java.util.Map;

public class ErrorCodeLookUp {

    private ErrorCodeLookUp() {}

    private static final Map<String, String> value;

    static {
        value = new HashMap<>();
        value.put("4000", "ENTITY_NOT_FOUND_EXCEPTION");
    }

    public static String getMessageKey(String errorCode) {
        return value.getOrDefault(errorCode, "GENERIC_EXCEPTION");
    }
}
