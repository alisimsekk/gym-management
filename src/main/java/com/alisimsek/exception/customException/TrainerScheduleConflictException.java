package com.alisimsek.exception.customException;

import com.alisimsek.exception.BaseException;
import org.springframework.http.HttpStatus;

public class TrainerScheduleConflictException extends BaseException {
    public TrainerScheduleConflictException() {
        super(HttpStatus.CONFLICT, "4009");
    }
}
