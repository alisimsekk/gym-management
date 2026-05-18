package com.alisimsek.exception.customException;

import com.alisimsek.exception.BaseException;
import org.springframework.http.HttpStatus;

public class TraineeScheduleConflictException extends BaseException {
    public TraineeScheduleConflictException() {
        super(HttpStatus.CONFLICT, "4010");
    }
}
