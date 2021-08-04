package es.caib.helium.domini.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.helium.domini.model.DominiDto;
import es.caib.helium.domini.model.PagedList;
import es.caib.helium.domini.model.ResultatDomini;
import es.caib.helium.domini.service.DominiService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.json.patch.JsonPatchPatchConverter;
import org.springframework.data.rest.webmvc.json.patch.Patch;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Map;

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
     * @param pageable Informació de paginació. Exemple: {@code page=0&size=25}
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
    public ResponseEntity<PagedList<DominiDto>> listDominisV1(
            @RequestParam(value = "entornId") Long entornId,
            @RequestParam(value = "filtre", required = false) String filtre,
            @RequestParam(value = "expedientTipusId", required = false) Long expedientTipusId,
            @RequestParam(value = "expedientTipusPareId", required = false) Long expedientTipusPareId,
            final Pageable pageable,
            final Sort sort) {

        log.debug("[CTR] llistant dominis: \n" +
                "entornId: " + entornId +
                "expedientTipusId: " + expedientTipusId +
                "expedientTipusPareId: " + expedientTipusPareId +
                "filtre: " + filtre);

        PagedList<DominiDto> dominiList = dominiService.listDominis(
                entornId,
                expedientTipusId,
                expedientTipusPareId,
                filtre,
                pageable,
                sort);
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
//            @RequestParam(value = "identificador", required = false) String identificador,
            @RequestParam(required = false) Map<String, String> parametres) {

        log.debug("[CTR] consulta el domini: " + dominiId);

        String identificador = null;
        if (parametres != null) {
            identificador = parametres.get("identificador");
            parametres.remove("identificador");
        }

        return new ResponseEntity<>(
                dominiService.consultaDomini(
                        dominiId,
                        identificador,
                        parametres),
                HttpStatus.OK);

    }


}
