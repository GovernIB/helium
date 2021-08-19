package es.caib.helium.expedient.controller;

import es.caib.helium.expedient.domain.Expedient;
import es.caib.helium.expedient.service.ExpedientService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(ImportacioController.API_PATH)
public class ImportacioController {

    public static final String API_PATH = "/importacio/expedients";

    private final ExpedientService expedientService;

    @PostMapping(value = "importar", consumes = "application/json")
    public ResponseEntity<Void> importarExpedients(
            @Valid @RequestBody List<Expedient> expedients, BindingResult errors) throws Exception {

        if (errors.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        System.out.println("IMPORTACIO " + expedients.size());
        expedientService.importarExpedients(expedients);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
