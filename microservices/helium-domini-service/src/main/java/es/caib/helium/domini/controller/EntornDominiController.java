package es.caib.helium.domini.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.helium.domini.model.DominiDto;
import es.caib.helium.domini.model.PagedList;
import es.caib.helium.domini.service.DominiService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(EntornDominiController.API_PATH)
public class EntornDominiController {

    public static final String API_PATH = "/api/v1/entorns/{entornId}/dominis";

    private final DominiService dominiService;
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;
    private final SmartValidator smartValidator;


    @GetMapping(produces = { "application/json" })
    public ResponseEntity<PagedList> listDominisV1(
            @PathVariable("entornId") Long entornId,
            @RequestParam(value = "filtre", required = false) String filtre,
            final Pageable pageable,
            final Sort sort) {

        PagedList<DominiDto> dominiList = dominiService.listDominisByEntorn(
                entornId,
                filtre,
                pageable,
                sort);
        if (dominiList.getTotalElements() == 0)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<PagedList>(dominiList, HttpStatus.OK);
    }

    @GetMapping(value = "/{codi}")
    public ResponseEntity<DominiDto> getDominiByEntornAndCodi(
            @PathVariable("entornId") Long entornId,
            @PathVariable("codi") String codi) {

        return new ResponseEntity<>(dominiService.getByEntornAndCodi(entornId, codi), HttpStatus.OK);
    }

}
