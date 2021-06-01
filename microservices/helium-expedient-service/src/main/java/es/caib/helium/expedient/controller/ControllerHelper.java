package es.caib.helium.expedient.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;

/**
 * Classe d'ajuda per als controladors
 */
@Slf4j
public class ControllerHelper {

    /**
     *
     * @param bindingResult Resultat de validació
     * @return Retorna un string amb tota la informació de validació, indicant els camps qeu han donat error
     */
    public static String getValidationErrorMessage(BindingResult bindingResult) {
        StringBuilder sb = new StringBuilder();
        sb.append(bindingResult.getErrorCount()).append(" error(s): ");

        bindingResult.getAllErrors().forEach(error -> sb.append("[").append(error).append("] "));
        return sb.toString();
    }

}
