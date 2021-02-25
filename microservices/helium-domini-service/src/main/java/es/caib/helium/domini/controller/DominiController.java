package es.caib.helium.domini.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.helium.domini.model.DominiDto;
import es.caib.helium.domini.model.PagedList;
import es.caib.helium.domini.model.ResultatDomini;
import es.caib.helium.domini.service.DominiService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@RestController
@Slf4j
@AllArgsConstructor
//@RequestMapping("/api/v1/dominis")
@RequestMapping(DominiController.API_PATH)
public class DominiController {

    public static final String API_PATH = "/api/v1";

    private final DominiService dominiService;
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;
    private final SmartValidator smartValidator;

    @GetMapping
    public String test() {
        return "Domini-service working!";
    }

    @PostMapping(value = "/entorns/{entornId}/dominis", consumes = { "application/json" })
    public ResponseEntity<Void> createDominiV1(
            @PathVariable("entornId") Long entornId,
            @Valid @RequestBody DominiDto dominiDto) {

        dominiDto.setEntorn(entornId);
        DominiDto savedDto = dominiService.createDomini(dominiDto);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Location", API_PATH + "/" + savedDto.getId());
        return new ResponseEntity<>(HttpStatus.CREATED);

    }

    @PutMapping(value = "/entorns/{entornId}/dominis/{dominiId}", consumes = { "application/json" })
    public ResponseEntity<Void> updateDominiV1(
            @PathVariable("entornId") Long entornId,
            @PathVariable("dominiId") Long dominiId,
            @Valid @RequestBody DominiDto dominiDto) {

        dominiService.updateDomini(
                entornId,
                dominiId,
                dominiDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @PatchMapping(value = "/entorns/{entornId}/dominis/{dominiId}", consumes = { "application/json" })
    public ResponseEntity<Void> patchDominiV1(
            HttpServletRequest request,
            @PathVariable("entornId") Long entornId,
            @PathVariable("dominiId") Long dominiId,
            @RequestBody JsonNode dominiJson,
            BindingResult bindingResult) {
//        String accept = request.getHeader("Accept");

        Patch patch = new JsonPatchPatchConverter(objectMapper).convert(dominiJson);
        DominiDto dominiDto = dominiService.getById(entornId, dominiId);
        DominiDto patchedDominiDto = patch.apply(
                dominiDto,
                DominiDto.class
        );
        smartValidator.validate(patchedDominiDto, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ControllerHelper.getValidationErrorMessage(bindingResult));
        } else {
            dominiService.updateDomini(
                    entornId,
                    dominiId,
                    patchedDominiDto);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(value = "/entorns/{entornId}/dominis/{dominiId}")
    public ResponseEntity<Void> deleteDominiV1(
            @PathVariable("entornId") Long entornId,
            @PathVariable("dominiId") Long dominiId) {

        dominiService.delete(entornId, dominiId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @GetMapping(value = "/entorns/{entornId}/dominis/{dominiId}")
    public ResponseEntity<DominiDto> getDominiV1(
            @PathVariable("entornId") Long entornId,
            @PathVariable("dominiId") Long dominiId) {

        return new ResponseEntity<>(dominiService.getById(entornId, dominiId), HttpStatus.OK);

    }

    @GetMapping(value = "/entorns/{entornId}/dominis", produces = { "application/json" })
    public ResponseEntity<PagedList> listDominisV1(
            @PathVariable("entornId") Long entornId,
            @RequestParam(value = "filtre", required = false) String filtre,
            @RequestParam(value = "expedientTipusId", required = false) Long expedientTipusId,
            @RequestParam(value = "expedientTipusPareId", required = false) Long expedientTipusPareId,
            final Pageable pageable,
            final Sort sort) {

        PagedList<DominiDto> dominiList = dominiService.listDominis(
                entornId,
                expedientTipusId,
                expedientTipusPareId,
                filtre,
                pageable,
                sort);
        if (dominiList.getTotalElements() == 0)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<PagedList>(dominiList, HttpStatus.OK);
    }

//    @GetMapping(value = "/entorns/{entornId}/dominis/{codi}")
//    public ResponseEntity<DominiDto> getDominiByEntornAndCodi(
//            @PathVariable("entornId") Long entornId,
//            @PathVariable("codi") String codi) {
//
//        return new ResponseEntity<>(dominiService.getByEntornAndCodi(entornId, codi), HttpStatus.OK);
//    }


    // Dominis per tipus d'expedient
    // //////////////////////////////////////////////

    @GetMapping(value = "/expedientTipus/{expedientTipusId}/dominis/{codi}")
    ResponseEntity<DominiDto> getDominiByExpedientTipusAndCodi(
            @PathVariable("expedientTipusId") Long expedientTipusId,
            @PathVariable("codi") String codi,
            @RequestParam(value = "expedientTipusPareId", required = false) Long expedientTipusPareId) {

        return new ResponseEntity<>(
                dominiService.getByExpedientTipusAndCodi(expedientTipusId, expedientTipusPareId, codi),
                HttpStatus.OK);
    }

    @GetMapping(value = "/expedientTipus/{expedientTipusId}/dominis")
    ResponseEntity<PagedList> llistaDominiByExpedientTipus(
            @PathVariable("expedientTipusId") Long expedientTipusId,
            @RequestParam(value = "filtre", required = false) String filtreRsql,
            final Pageable pageable,
            final Sort sort) {

        PagedList<DominiDto> dominiList = dominiService.listDominisByExpedientTipus(
                expedientTipusId,
                filtreRsql,
                pageable,
                sort);
        if (dominiList.getTotalElements() == 0)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<PagedList>(dominiList, HttpStatus.OK);
    }


    // Consulta de Dominis
    // //////////////////////////////////////////////

    @GetMapping(value = "/entorns/{entornId}/dominis/{dominiId}/resultats")
    public ResponseEntity<ResultatDomini> consultaDominiV1(
            @PathVariable("entornId") Long entornId,
            @PathVariable("dominiId") Long dominiId,
            @RequestParam(value = "identificador", required = false) String identificador,
            @RequestParam(value = "parametres", required = false) Map<String, Object> parametres) {

        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<ResultatDomini>(objectMapper.readValue("[ {\n  \"valor\" : \"valor\",\n  \"codi\" : \"codi\"\n}, {\n  \"valor\" : \"valor\",\n  \"codi\" : \"codi\"\n} ]", ResultatDomini.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<ResultatDomini>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<ResultatDomini>(HttpStatus.NOT_IMPLEMENTED);
    }


}
