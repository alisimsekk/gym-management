package com.alisimsek.exception;

public class ExceptionMessage {

    private ExceptionMessage() {
    }

    public static String getEntityNotFoundMessage(String entityName, Long id) {
        return entityName.concat(" not found with ID: ").concat(String.valueOf(id));
    }
}
