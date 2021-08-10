package net.conselldemallorca.helium.jbpm3.integracio;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(HeliumJbpmException.class)
    public ResponseEntity handleException(HeliumJbpmException e) {
        return new ResponseEntity(e.getMessage(), e.getHttpStatus());
    }
}
