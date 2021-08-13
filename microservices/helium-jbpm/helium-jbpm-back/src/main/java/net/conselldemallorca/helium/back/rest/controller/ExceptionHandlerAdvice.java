package net.conselldemallorca.helium.back.rest.controller;

import net.conselldemallorca.helium.api.exception.HeliumJbpmException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(HeliumJbpmException.class)
    public ResponseEntity handleException(HeliumJbpmException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(e.getHttpStatus()));
    }
}
