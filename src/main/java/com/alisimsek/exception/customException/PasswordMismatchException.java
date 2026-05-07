package com.alisimsek.exception.customException;

import com.alisimsek.exception.BaseException;
import org.springframework.http.HttpStatus;

public class PasswordMismatchException extends BaseException {
    public PasswordMismatchException() {
        super(HttpStatus.BAD_REQUEST, "4003");
    }
}
