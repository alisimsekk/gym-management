package com.alisimsek.exception.customException;

import com.alisimsek.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UserLockedException extends BaseException {
    public UserLockedException() {
        super(HttpStatus.UNAUTHORIZED, "4005");
    }
}
