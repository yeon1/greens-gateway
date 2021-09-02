package com.gateway.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GreensClientException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String originMessage;

    public GreensClientException(HttpStatus httpStatus, String message) {
        this(httpStatus, message, null);
    }

    public GreensClientException(HttpStatus httpStatus, String message, String originMessage) {
        super(message);
        this.httpStatus = httpStatus;
        this.originMessage = originMessage;
    }
}
