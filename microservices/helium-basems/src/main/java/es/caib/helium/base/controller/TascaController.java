package es.caib.helium.base.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.caib.helium.client.expedient.tasca.TascaClientService;
import es.caib.helium.client.expedient.tasca.model.TascaDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(TascaController.API_PATH)
public class TascaController {

    public static final String API_PATH = "/api/v1/base/tasques";
    public final TascaClientService tascaService;

    @GetMapping(value = "/{tascaId}")
    public ResponseEntity<TascaDto> getTascaV1(
            @PathVariable("tascaId") String tascaId) {

        var tasca = tascaService.getTascaV1(tascaId);
        return new ResponseEntity<>(tasca, HttpStatus.OK);
    }
}
