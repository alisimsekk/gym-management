package com.alisimsek.exception.customException;

import com.alisimsek.exception.BaseException;
import org.springframework.http.HttpStatus;

public class EntityAlreadyExistsException extends BaseException {
    public EntityAlreadyExistsException() {
        super(HttpStatus.BAD_REQUEST, "4001");
    }
}
