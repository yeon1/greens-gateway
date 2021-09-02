package com.gateway.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GeneralExceptionHandler {

    @ExceptionHandler(GreensClientException.class)
    public ResponseEntity<ErrorResponse> handleGreensClientException(GreensClientException e) {
        log.error(e.getMessage());
        log.error(e.getOriginMessage());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), headers, e.getHttpStatus());
    }
}
