package com.alisimsek.exception.customException;

import com.alisimsek.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UserMismatchException extends BaseException {
    public UserMismatchException() {
        super(HttpStatus.BAD_REQUEST, "4004");
    }
}
