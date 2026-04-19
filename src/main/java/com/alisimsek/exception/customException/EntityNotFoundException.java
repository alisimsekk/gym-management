package com.alisimsek.exception.customException;

import com.alisimsek.exception.BaseException;
import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends BaseException {

    public EntityNotFoundException(String entityName) {
        super(entityName.concat(" not found"), HttpStatus.NOT_FOUND, "4000", new Object[] { entityName });
    }
}
