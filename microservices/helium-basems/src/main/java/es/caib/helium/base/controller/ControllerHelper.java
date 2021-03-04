package es.caib.helium.base.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;

@Slf4j
public class ControllerHelper {

    public static String getValidationErrorMessage(BindingResult bindingResult) {
        StringBuilder sb = new StringBuilder();
        sb.append(bindingResult.getErrorCount()).append(" error(s): ");

        bindingResult.getAllErrors().forEach(error -> sb.append("[").append(error).append("] "));
        return sb.toString();
    }

}
