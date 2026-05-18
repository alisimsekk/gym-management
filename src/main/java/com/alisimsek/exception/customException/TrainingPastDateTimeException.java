package com.alisimsek.exception.customException;

import com.alisimsek.exception.BaseException;
import org.springframework.http.HttpStatus;

public class TrainingPastDateTimeException extends BaseException {
    public TrainingPastDateTimeException() {
        super(HttpStatus.BAD_REQUEST, "4008");
    }
}
