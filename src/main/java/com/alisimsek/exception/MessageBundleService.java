package com.alisimsek.exception;

import java.util.Locale;

public interface MessageBundleService {

    String getMessage(String code, Object... args);

    String getMessage(String code, Locale locale, Object... args);
}
