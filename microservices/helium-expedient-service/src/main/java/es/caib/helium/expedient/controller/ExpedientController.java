package es.caib.helium.expedient.controller;

import java.util.Date;

import javax.validation.Valid;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.json.patch.JsonPatchPatchConverter;
import org.springframework.data.rest.webmvc.json.patch.Patch;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
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

import es.caib.helium.expedient.model.ExpedientDto;
import es.caib.helium.expedient.model.ExpedientEstatTipusEnum;
import es.caib.helium.expedient.model.MostrarAnulatsEnum;
import es.caib.helium.expedient.service.ExpedientService;
import es.caib.helium.ms.controller.ControllerHelper;
import es.caib.helium.ms.model.PagedList;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador que defineix la API REST d'expedients per la consulta paginada i els
 * mètodes per crear, actualitzar o esborrar la informació d'expedients dins del
 * micro servei d'expedients i tasques.
 * 
 */
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(ExpedientController.API_PATH)
public class ExpedientController {

    public static final String API_PATH = "/api/v1/expedients";

    private final ExpedientService expedientService;
    private final ObjectMapper objectMapper;
    private final SmartValidator smartValidator;

    /**
     * Consulta amb paràmetres corresponent al llistat d'expedients.
    * La cerca pot rebre paràmetres per:
    * <ul>
    *     <li>ordenar</li>
    *     <li>paginar</li>
    *     <li>filtrar (amb el mateixos paràmetres que en el llistat)</li>
    * </ul>
    */
   @GetMapping(produces = { "application/json" })
   public ResponseEntity<PagedList<ExpedientDto>> findExpedientsAmbFiltrePaginatV1(
           @RequestParam(value = "entornId") Long entornId,
           @RequestParam(value = "filtre", required = false) String filtre,
           @RequestParam(value = "usuariCodi", required = false) String usuariCodi,
           @RequestParam(value = "expedientTipusId", required = false) Long expedientTipusId,
           @RequestParam(value = "titol", required = false) String titol,
           @RequestParam(value = "numero", required = false) String numero,
           @RequestParam(value = "dataInici1", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataInici1,
           @RequestParam(value = "dataInici2", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataInici2,
           @RequestParam(value = "dataFi1", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataFi1,
           @RequestParam(value = "dataFi2", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataFi2,
           @RequestParam(value = "estatTipus", required = false) ExpedientEstatTipusEnum estatTipus,
           @RequestParam(value = "estatId", required = false) Long estatId,
           @RequestParam(value = "nomesTasquesPersonals", required = false, defaultValue = "false") boolean nomesTasquesPersonals,
           @RequestParam(value = "nomesTasquesGrup", required = false, defaultValue = "false") boolean nomesTasquesGrup,
           @RequestParam(value = "nomesAlertes", required = false, defaultValue = "false") boolean nomesAlertes,
           @RequestParam(value = "nomesErrors", required = false, defaultValue = "false") boolean nomesErrors,
           @RequestParam(value = "mostrarAnulats", required = false) MostrarAnulatsEnum mostrarAnulats,
           final Pageable pageable,	
           final Sort sort) {

       log.debug("[CTR] llistant expedients: \n" +
    		   "usuariCodi: " + usuariCodi +
               ", entornId: " + entornId +
               ", expedientTipusId: " + expedientTipusId +
               ", titol: " + titol +
               ", numero: " + numero +
               ", dataInici1: " + dataInici1 +
               ", dataInici2: " + dataInici2 +
               ", dataFi1: " + dataFi1 +
               ", dataFi2: " + dataFi2 +
               ", estatTipus: " + estatTipus +
               ", estatId: " + estatId +
               ", nomesTasquesPersonals: " + nomesTasquesPersonals +
               ", nomesTasquesGrup: " + nomesTasquesGrup +
               ", expedientTipusId: " + nomesAlertes +
               ", nomesErrors: " + nomesErrors +
               ", mostrarAnulats: " + mostrarAnulats + 
               ", filtre: " + filtre);

       PagedList<ExpedientDto> expedientList = expedientService.listExpedients(
    		   usuariCodi,
    		   entornId,
    		   expedientTipusId, 
    		   titol, 
    		   numero, 
    		   dataInici1, 
    		   dataInici2, 
    		   dataFi1, 
    		   dataFi2, 
    		   estatTipus, 
    		   estatId, 
    		   nomesTasquesPersonals, 
    		   nomesTasquesGrup, 
    		   nomesAlertes, 
    		   nomesErrors, 
    		   mostrarAnulats, 
    		   filtre, 
    		   pageable, 
    		   sort);
    		   
       if (expedientList.getTotalElements() == 0)
           return new ResponseEntity<>(HttpStatus.NO_CONTENT);
       
       return new ResponseEntity<>(expedientList, HttpStatus.OK);
   }

    @PostMapping(consumes = { "application/json" })
    public ResponseEntity<Void> createExpedientV1(
            @Valid @RequestBody ExpedientDto expedientDto) {

        log.debug("[CTR] create expedient: " + expedientDto.toString());
        ExpedientDto savedDto;

        try {
            savedDto = expedientService.createExpedient(expedientDto);
        } catch (DataIntegrityViolationException ex) {
            log.error("[CTR] Create: Ja existeix un expedient amb el mateix identificador", ex);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ja existeix un expedient amb el mateix entorn, tipus d'expedient i codi");
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Location", API_PATH + "/" + savedDto.getId());
        return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);

    }

    @PutMapping(value = "/{expedientId}", consumes = { "application/json" })
    public ResponseEntity<Void> updateExpedientV1(
            @PathVariable("expedientId") Long expedientId,
            @Valid @RequestBody ExpedientDto expedientDto) {

        log.debug("[CTR] update expedient: " + expedientDto.toString());

        try {
            expedientService.updateExpedient(
                    expedientId,
                    expedientDto);
        } catch (DataIntegrityViolationException ex) {
            log.error("[CTR] Update: Ja existeix un expedient amb el mateix entorn, tipus d'expedient i codi", ex);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ja existeix un expedient amb el mateix entorn, tipus d'expedient i codi");
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @PatchMapping(value = "/{expedientId}", consumes = { "application/json" })
    public ResponseEntity<Void> patchExpedientV1(
//            HttpServletRequest request,
            @PathVariable("expedientId") Long expedientId,
            @RequestBody JsonNode expedientJson,
            BindingResult bindingResult) {

        log.debug("[CTR] patch expedient: " + expedientId);

        Patch patch = new JsonPatchPatchConverter(objectMapper).convert(expedientJson);
        ExpedientDto expedientDto = expedientService.getById(expedientId);
        ExpedientDto patchedExpedientDto = patch.apply(
                expedientDto,
                ExpedientDto.class
        );
        smartValidator.validate(patchedExpedientDto, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ControllerHelper.getValidationErrorMessage(bindingResult));
        } else {
            expedientService.updateExpedient(
                    expedientId,
                    patchedExpedientDto);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(value = "/{expedientId}")
    public ResponseEntity<Void> deleteExpedientV1(
            @PathVariable("expedientId") Long expedientId) {

        log.debug("[CTR] delete expedient: " + expedientId);

        expedientService.delete(expedientId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @GetMapping(value = "/{expedientId}")
    public ResponseEntity<ExpedientDto> getExpedientV1(
            @PathVariable("expedientId") Long expedientId) {

        log.debug("[CTR] get expedient: " + expedientId);
        return new ResponseEntity<>(expedientService.getById(expedientId), HttpStatus.OK);

    }
}
