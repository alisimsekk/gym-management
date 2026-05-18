package com.alisimsek.exception.customException;

import com.alisimsek.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidTrainingTimeSlotException extends BaseException {
    public InvalidTrainingTimeSlotException() {
        super(HttpStatus.BAD_REQUEST, "4006");
    }
}
