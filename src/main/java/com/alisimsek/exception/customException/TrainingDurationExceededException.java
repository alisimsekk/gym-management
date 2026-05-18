package com.alisimsek.exception.customException;

import com.alisimsek.exception.BaseException;
import org.springframework.http.HttpStatus;

public class TrainingDurationExceededException extends BaseException {
    public TrainingDurationExceededException() {
        super(HttpStatus.BAD_REQUEST, "4007");
    }
}
