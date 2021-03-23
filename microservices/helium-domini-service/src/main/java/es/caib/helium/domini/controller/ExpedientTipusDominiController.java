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
@RequestMapping(ExpedientTipusDominiController.API_PATH)
public class ExpedientTipusDominiController {

    public static final String API_PATH = "/api/v1/expedientTipus/{expedientTipusId}/dominis";

    private final DominiService dominiService;
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;
    private final SmartValidator smartValidator;


    @GetMapping()
    ResponseEntity<PagedList> llistaDominiByExpedientTipus(
            @PathVariable("expedientTipusId") Long expedientTipusId,
            @RequestParam(value = "filtre", required = false) String filtreRsql,
            final Pageable pageable,
            final Sort sort) {

        log.debug("[CTR] llistant dominis per tipus expedient: \n" +
                "expedientTipusId: " + expedientTipusId +
                "filtre: " + filtreRsql);

        PagedList<DominiDto> dominiList = dominiService.listDominisByExpedientTipus(
                expedientTipusId,
                filtreRsql,
                pageable,
                sort);
        if (dominiList.getTotalElements() == 0)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<PagedList>(dominiList, HttpStatus.OK);
    }

    @GetMapping(value = "/{codi}")
    ResponseEntity<DominiDto> getDominiByExpedientTipusAndCodi(
            @PathVariable("expedientTipusId") Long expedientTipusId,
            @PathVariable("codi") String codi,
            @RequestParam(value = "expedientTipusPareId", required = false) Long expedientTipusPareId) {

        log.debug("[CTR] get domini per expedientTipus i codi: " +
                "expedientTipusId: " + expedientTipusId +
                "codi: " + codi);

        return new ResponseEntity<>(
                dominiService.getByExpedientTipusAndCodi(expedientTipusId, expedientTipusPareId, codi),
                HttpStatus.OK);
    }

}
