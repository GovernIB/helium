package es.caib.helium.domini.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.webmvc.json.patch.JsonPatchPatchConverter;
import org.springframework.data.rest.webmvc.json.patch.Patch;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.helium.domini.model.ConsultaDominisDades;
import es.caib.helium.domini.model.DominiDto;
import es.caib.helium.domini.model.FilaResultat;
import es.caib.helium.domini.model.PagedList;
import es.caib.helium.domini.model.ParellaCodiValor;
import es.caib.helium.domini.model.ResultatDomini;
import es.caib.helium.domini.service.DominiService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador que defineix la API REST de dominis
 */
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(DominiController.API_PATH)
public class DominiController {

    public static final String API_PATH = "/api/v1/dominis";

    private final DominiService dominiService;
    private final ObjectMapper objectMapper;
//    private final HttpServletRequest request;
    private final SmartValidator smartValidator;

    /**
     *
     * @param entornId Identificador de l'entorn al que pertanyen els dominis consultats
     * @param filtre Filtre a aplicar en la consulta. El filtre té format RSQL. {@see https://github.com/jirutka/rsql-parser}.
     *               Exemple: {@code nom=ic=*domini*,codi==DOM}
     * @param expedientTipusId Tipus d'expedient al que pertanyen els dominis consultat. També es retornen els dominis
     *                         globals (sense tipus d'expedient definit
     * @param expedientTipusPareId Tipus d'expedient pare, en el cas que es faci una consulta tenint en compte la herència.
     *                             Es retornaran els dominis globals, els dominis que pertanyen al tipus d'expedient
     *                             ({@code expedientTipusId}), i en cas que el tipus d'expedient no tingui definit el domini,
     *                             es retornarà el de l'expedient pare.
     * @param pageable Informació de paginació. Exemple: {@code page=0&size=25}&sort=nom,desc
     * @param sort Informació de ordre Exemple: {@code nom,desc}
     * @return Retorna una pàgina del llistat de dominis, un cop aplicats els camps de filtre.
     *<br/>
     * La cerca pot rebre paràmetres per:
     * <ul>
     *     <li>ordenar</li>
     *     <li>paginar</li>
     *     <li>filtrar (utilitzant sintaxi rsql)</li>
     * </ul>
     */
    @GetMapping(produces = { "application/json" })
    public ResponseEntity<PagedList<DominiDto>> listDominis(
            @Valid ConsultaDominisDades consultaDominisDades) {

        log.debug("[CTR] llistant dominis: \n" +
                "entornId: " + consultaDominisDades.getEntornId() +
                "expedientTipusId: " + consultaDominisDades.getExpedientTipusId() +
                "expedientTipusPareId: " + consultaDominisDades.getExpedientTipusPareId() +
                "filtre: " + consultaDominisDades.getFiltre());

        PagedList<DominiDto> dominiList = dominiService.listDominis(
                consultaDominisDades.getEntornId(),
                consultaDominisDades.getExpedientTipusId(),
                consultaDominisDades.getExpedientTipusPareId(),
                consultaDominisDades.getFiltre(),
                consultaDominisDades.getPageable(),
                consultaDominisDades.getSort());
        if (dominiList.getTotalElements() == 0)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(dominiList, HttpStatus.OK);
    }


    @PostMapping(consumes = { "application/json" })
    public ResponseEntity<Long> createDominiV1(
            @Valid @RequestBody DominiDto dominiDto) {

        log.debug("[CTR] create domini: " + dominiDto.toString());
        DominiDto savedDto;

        if (dominiDto.getEntorn() == null) {
            log.error("[CTR] Create: El camp Domini.entornId és obligatori");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El camp Domini.entornId és obligatori");
        }
        try {
            savedDto = dominiService.createDomini(dominiDto);
        } catch (DataIntegrityViolationException ex) {
            log.error("[CTR] Create: Ja existeix un domini amb el mateix entorn, tipus d'expedient i codi", ex);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ja existeix un domini amb el mateix entorn, tipus d'expedient i codi");
        }

        //HttpHeaders httpHeaders = new HttpHeaders();
        //httpHeaders.add("Location", API_PATH + "/" + savedDto.getId());
        return new ResponseEntity<>(savedDto.getId(), HttpStatus.CREATED);

    }

    @PutMapping(value = "/{dominiId}", consumes = { "application/json" })
    public ResponseEntity<Void> updateDominiV1(
            @PathVariable("dominiId") Long dominiId,
            @Valid @RequestBody DominiDto dominiDto) {

        log.debug("[CTR] update domini: " + dominiDto.toString());

        try {
            dominiService.updateDomini(
                    dominiId,
                    dominiDto);
        } catch (DataIntegrityViolationException ex) {
            log.error("[CTR] Update: Ja existeix un domini amb el mateix entorn, tipus d'expedient i codi", ex);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ja existeix un domini amb el mateix entorn, tipus d'expedient i codi");
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @PatchMapping(value = "/{dominiId}", consumes = { "application/json" })
    public ResponseEntity<Void> patchDominiV1(
//            HttpServletRequest request,
            @PathVariable("dominiId") Long dominiId,
            @RequestBody JsonNode dominiJson,
            BindingResult bindingResult) {

        log.debug("[CTR] patch domini: " + dominiId);

        Patch patch = new JsonPatchPatchConverter(objectMapper).convert(dominiJson);
        DominiDto dominiDto = dominiService.getById(dominiId);
        DominiDto patchedDominiDto = patch.apply(
                dominiDto,
                DominiDto.class
        );
        smartValidator.validate(patchedDominiDto, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ControllerHelper.getValidationErrorMessage(bindingResult));
        } else {
            dominiService.updateDomini(
                    dominiId,
                    patchedDominiDto);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(value = "/{dominiId}")
    public ResponseEntity<Void> deleteDominiV1(
            @PathVariable("dominiId") Long dominiId) {

        log.debug("[CTR] delete domini: " + dominiId);

        dominiService.delete(dominiId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @GetMapping(value = "/{dominiId}")
    public ResponseEntity<DominiDto> getDominiV1(
            @PathVariable("dominiId") Long dominiId) {

        log.debug("[CTR] get domini: " + dominiId);
        return new ResponseEntity<>(dominiService.getById(dominiId), HttpStatus.OK);

    }


    // Consulta de Dominis
    // //////////////////////////////////////////////

    @GetMapping(value = "/{dominiId}/resultats")
    public ResponseEntity<ResultatDomini> consultaDominiV1(
            @PathVariable("dominiId") Long dominiId,
            @RequestParam(required = false) Map<String, String> parametres) {

        log.debug("[CTR] consulta el domini: " + dominiId);

        String identificador = null;
        if (parametres != null) {
            identificador = parametres.get("identificador");
            parametres.remove("identificador");
        }

        if (identificador == null) {
            log.error("Error consultat el domini id=" + dominiId + ". No s'ha indicat l'identificador");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "És obligatori indicar un paràmetre amb el nom identificador, que contingui l'identificador del domini");
        }
        try {
            var resposta = dominiService.consultaDomini(
                    dominiId,
                    identificador,
                    parametres).get();
            return new ResponseEntity<>(resposta, HttpStatus.OK);
        } catch (ResponseStatusException rex) {
            throw rex;
        } catch (Exception e) {
            String errorMsg = "Error consultat el domini " +
                    "id=" + dominiId + ", " +
                    "identificador=" + identificador + "). ";
            log.error(errorMsg, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al consultar el domini " + dominiId, e);
        }

    }

    @PostMapping(value = "resultats")
    public ResponseEntity<List<ResultatDomini>> consultaDominisV1(
            @RequestBody List<ConsultaDominiDada> consultaDominiDades) {

        List<ResultatDomini> resultatDominis = new ArrayList<>();
        List<CompletableFuture<ResultatDomini>> futureList = new ArrayList<>();

        if (consultaDominiDades == null || consultaDominiDades.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "És obligatori indicar com a mínim un domini a consultar");
        }
        for (var consultaDominiDada: consultaDominiDades) {
            log.debug("[CTR] consulta el domini: " + consultaDominiDada.getDominiId());

            String identificador = null;
            if (consultaDominiDada.getParametres() != null) {
                identificador = consultaDominiDada.getParametres().get("identificador");
                consultaDominiDada.getParametres().remove("identificador");
            }

            if (identificador == null) {
                log.error("Error consultat el domini id=" + consultaDominiDada.getDominiId() + ". No s'ha indicat l'identificador");
            }
            futureList.add(dominiService.consultaDomini(
                    consultaDominiDada.getDominiId(),
                    identificador,
                    consultaDominiDada.getParametres()));
        }

        // Esperam a que totes les peticions hagin acabat
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()]));

        resultatDominis = futureList.stream().map(f ->
                f.exceptionally(e -> {
                    log.error("Error consultant domini", e);
                    ResultatDomini resultatExcepcio = new ResultatDomini();
                    FilaResultat filaResultat = new FilaResultat();
                    ParellaCodiValor parellaCodiValor = ParellaCodiValor.builder()
                            .codi("domini-exception")
                            .valor(e.getMessage())
                            .build();
                    filaResultat.getColumnes().add(parellaCodiValor);
                    resultatExcepcio.add(filaResultat);
                    return resultatExcepcio;
                }).join())
                .collect(Collectors.toList());

        return new ResponseEntity<>(resultatDominis, HttpStatus.OK);
    }

    @Data
    @Builder
    public static class ConsultaDominiDada {
        private Long dominiId;
        private Map<String, String> parametres;
    }

}
