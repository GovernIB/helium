package es.caib.helium.domini.controller;

import es.caib.helium.domini.model.FilaResultat;
import es.caib.helium.domini.model.ParellaCodiValor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controlador amb un domini REST d'exemple, que retorna com a resposta una llista de 3 FilaResultat
 * on a cada FilaResultat es troben com a columnes els paràmetres rebuts
 */
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(DominiConsultaTestController.API_PATH)
public class DominiConsultaTestController {

    public static final String API_PATH = "/api/domini/rest";

    /**
     * Domini REST de test
     *
     * @param allParams Mapa amb els paràmetres passats per a la consulta de les dades del domini
     * @return Retorna com a resposta una llista de 3 FilaResultat on a cada FilaResultat es troben com a columnes els
     * paràmetres rebuts en el paràmetre {@code allParams}
     */
    @GetMapping(produces = { "application/json" })
    public ResponseEntity<List<FilaResultat>> dominisRestTest(@RequestParam Map<String,String> allParams) {

        log.debug("[CTR] Executant consulta de test");
        List<FilaResultat> resultats = new ArrayList<>();
        FilaResultat fila = new FilaResultat();
        for (Map.Entry<String, String> entry: allParams.entrySet()) {
            fila.getColumnes().add(new ParellaCodiValor(entry.getKey(), entry.getValue()));
        }
        resultats.add(fila);
        resultats.add(fila);
        resultats.add(fila);

        return new ResponseEntity<>(resultats, HttpStatus.OK);
    }

}
