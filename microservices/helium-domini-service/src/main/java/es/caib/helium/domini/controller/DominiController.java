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
import java.util.Map;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(DominiController.API_PATH)
public class DominiController {

    public static final String API_PATH = "/api/v1/dominis";

    private final DominiService dominiService;
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;
    private final SmartValidator smartValidator;

    @GetMapping(produces = { "application/json" })
    public ResponseEntity<PagedList> listDominisV1(
            @RequestParam(value = "entornId", required = true) Long entornId,
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

    @PostMapping(consumes = { "application/json" })
    public ResponseEntity<Void> createDominiV1(
            @Valid @RequestBody DominiDto dominiDto) {

        if (dominiDto.getEntorn() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El camp Domini.entornId és obligatori");
        DominiDto savedDto = dominiService.createDomini(dominiDto);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Location", API_PATH + "/" + savedDto.getId());
        return new ResponseEntity<>(HttpStatus.CREATED);

    }

    @PutMapping(value = "/{dominiId}", consumes = { "application/json" })
    public ResponseEntity<Void> updateDominiV1(
            @PathVariable("dominiId") Long dominiId,
            @Valid @RequestBody DominiDto dominiDto) {

        dominiService.updateDomini(
                dominiId,
                dominiDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @PatchMapping(value = "/{dominiId}", consumes = { "application/json" })
    public ResponseEntity<Void> patchDominiV1(
            HttpServletRequest request,
            @PathVariable("dominiId") Long dominiId,
            @RequestBody JsonNode dominiJson,
            BindingResult bindingResult) {
//        String accept = request.getHeader("Accept");

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

        dominiService.delete(dominiId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @GetMapping(value = "/{dominiId}")
    public ResponseEntity<DominiDto> getDominiV1(
            @PathVariable("dominiId") Long dominiId) {

        return new ResponseEntity<>(dominiService.getById(dominiId), HttpStatus.OK);

    }


    // Consulta de Dominis
    // //////////////////////////////////////////////

    @GetMapping(value = "/{dominiId}/resultats")
    public ResponseEntity<ResultatDomini> consultaDominiV1(
            @PathVariable("dominiId") Long dominiId,
//            @RequestParam(value = "identificador", required = false) String identificador,
            @RequestParam(required = false) Map<String, String> parametres) {

        String identificador = null;
        if (parametres != null) {
            identificador = parametres.get("identificador");
            parametres.remove("identificador");
        }

        return new ResponseEntity(
                dominiService.consultaDomini(
                        dominiId,
                        identificador,
                        parametres),
                HttpStatus.OK);

    }


}
