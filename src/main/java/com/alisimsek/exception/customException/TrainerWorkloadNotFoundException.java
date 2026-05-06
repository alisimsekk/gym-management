package com.alisimsek.exception.customException;

import com.alisimsek.exception.BaseException;
import org.springframework.http.HttpStatus;

public class TrainerWorkloadNotFoundException extends BaseException {
    public TrainerWorkloadNotFoundException() {
        super(HttpStatus.NOT_FOUND, "4002");
    }
}
