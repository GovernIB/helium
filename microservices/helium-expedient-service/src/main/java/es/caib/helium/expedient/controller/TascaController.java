package es.caib.helium.expedient.controller;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

import es.caib.helium.expedient.model.TascaDto;
import es.caib.helium.expedient.service.TascaService;
import es.caib.helium.ms.controller.ControllerHelper;
import es.caib.helium.ms.model.PagedList;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador que defineix la API REST de tasques per la consulta paginada i els
 * mètodes per crear, actualitzar o esborrar la informació de les tasques..
 * 
 */
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(TascaController.API_PATH)
public class TascaController {

    public static final String API_PATH = "/api/v1/tasques";

    private final TascaService tascaService;
    private final ObjectMapper objectMapper;
    private final SmartValidator smartValidator;

    /**
     *
     * @param entornId Identificador de l'entorn al que pertanyen les tasques consultades
     * @param filtre Filtre a aplicar en la consulta. El filtre té format RSQL. {@see https://github.com/jirutka/rsql-parser}.
     *               Exemple: {@code nom=ic=*tasca*,codi==T1}
     * @param expedientTipusId Expedient tipus al que pertanyen les tasques consultades.
     * @param pageable Informació de paginació. Exemple: {@code page=0&size=25}
     * @param sort Informació de ordre Exemple: {@code nom,desc}
     * @return Retorna una pàgina del llistat de tasques, un cop aplicats els camps de filtre.
     *<br/>
     * La cerca pot rebre paràmetres per:
     * <ul>
     *     <li>ordenar</li>
     *     <li>paginar</li>
     *     <li>filtrar (utilitzant sintaxi rsql)</li>
     * </ul>
     */
    @GetMapping(produces = { "application/json" })
    public ResponseEntity<PagedList<TascaDto>> findTasquesAmbFiltrePaginatV1(
            @RequestParam(value = "entornId") Long entornId,
            @RequestParam(value = "expedientTipusId", required = false) Long expedientTipusId,
            @RequestParam(value = "usuariAssignat", required = false) String usuariAssignat,
            @RequestParam(value = "nom", required = false) String nom,
            @RequestParam(value = "titol", required = false) String titol,
            @RequestParam(value = "expedientId", required = false) Long expedientId,
            @RequestParam(value = "expedientTitol", required = false) String expedientTitol,
            @RequestParam(value = "expedientNumero", required = false) String expedientNumero,
            @RequestParam(value = "dataCreacioInici", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataCreacioInici,
            @RequestParam(value = "dataCreacioFi", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataCreacioFi,
            @RequestParam(value = "dataLimitInici", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataLimitInici,
            @RequestParam(value = "dataLimitFi", required = false) @DateTimeFormat(pattern="dd/MM/yyyy") Date dataLimitFi,
            @RequestParam(value = "mostrarAssignadesUsuari", required = false, defaultValue = "false") boolean mostrarAssignadesUsuari,
            @RequestParam(value = "mostrarAssignadesGrup", required = false, defaultValue = "false") boolean mostrarAssignadesGrup,
            @RequestParam(value = "nomesPendents", required = false, defaultValue = "false") boolean nomesPendents,
            @RequestParam(value = "filtre", required = false) String filtre,
                        
            final Pageable pageable,
            final Sort sort) {

        log.debug("[CTR] llistant tasques: \n" +
                "entornId: " + entornId +
                ", expedientTipusId: " + expedientTipusId +
                ", usuariAssignat: " + usuariAssignat +
                ", nom: " + nom +
                ", titol: " + titol +
                ", expedientId: " + expedientId +
                ", expedientTitol: " + expedientTitol +
                ", expedientNumero: " + expedientNumero +
                ", dataCreacioInici: " + dataCreacioInici +
                ", dataCreacioFi: " + dataCreacioFi +
                ", dataLimitInici: " + dataLimitInici +
                ", dataLimitFi: " + dataLimitFi +
                ", mostrarAssignadesUsuari: " + mostrarAssignadesUsuari +
                ", mostrarAssignadesGrup: " + mostrarAssignadesGrup +
                ", nomesPendents: " + nomesPendents +
                "filtre: " + filtre);

        PagedList<TascaDto> tascaList = tascaService.listTasques(
        		entornId, 
        		expedientTipusId,
        		usuariAssignat, 
        		nom, 
        		titol, 
        		expedientId, 
        		expedientTitol, 
        		expedientNumero, 
        		dataCreacioInici, 
        		dataCreacioFi, 
        		dataLimitInici, 
        		dataLimitFi, 
        		mostrarAssignadesUsuari, 
        		mostrarAssignadesGrup, 
        		nomesPendents, 
        		filtre, 
        		pageable, 
        		sort);
        if (tascaList.getTotalElements() == 0)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(tascaList, HttpStatus.OK);
    }

    @PostMapping(consumes = { "application/json" })
    public ResponseEntity<Void> createTascaV1(
            @Valid @RequestBody TascaDto tascaDto) {

        log.debug("[CTR] create tasca: " + tascaDto.toString());
        TascaDto savedDto;

        try {
            savedDto = tascaService.createTasca(tascaDto);
        } catch (DataIntegrityViolationException ex) {
            log.error("[CTR] Create: Ja existeix un tasca amb el mateix entorn, tipus d'tasca i codi", ex);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ja existeix un domini amb el mateix entorn, tipus d'tasca i codi");
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Location", API_PATH + "/" + savedDto.getId());
        return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);

    }

    @PutMapping(value = "/{tascaId}", consumes = { "application/json" })
    public ResponseEntity<Void> updateTascaV1(
            @PathVariable("tascaId") Long tascaId,
            @Valid @RequestBody TascaDto tascaDto) {

        log.debug("[CTR] update tasca: " + tascaDto.toString());

        tascaService.updateTasca(
                tascaId,
                tascaDto);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @PatchMapping(value = "/{tascaId}", consumes = { "application/json" })
    public ResponseEntity<Void> patchTascaV1(
//            HttpServletRequest request,
            @PathVariable("tascaId") Long tascaId,
            @RequestBody JsonNode tascaJson,
            BindingResult bindingResult) {

        log.debug("[CTR] patch tasca: " + tascaId);

        Patch patch = new JsonPatchPatchConverter(objectMapper).convert(tascaJson);
        TascaDto tascaDto = tascaService.getById(tascaId);
        TascaDto patchedTascaDto = patch.apply(
                tascaDto,
                TascaDto.class
        );
        smartValidator.validate(patchedTascaDto, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ControllerHelper.getValidationErrorMessage(bindingResult));
        } else {
            tascaService.updateTasca(
                    tascaId,
                    patchedTascaDto);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(value = "/{tascaId}")
    public ResponseEntity<Void> deleteTascaV1(
            @PathVariable("tascaId") Long tascaId) {

        log.debug("[CTR] delete tasca: " + tascaId);

        tascaService.delete(tascaId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @GetMapping(value = "/{tascaId}")
    public ResponseEntity<TascaDto> getTascaV1(
            @PathVariable("tascaId") Long tascaId) {

        log.debug("[CTR] get tasca: " + tascaId);
        return new ResponseEntity<>(tascaService.getById(tascaId), HttpStatus.OK);

    }
    
    
    /// Mètodes pels responsables
    
    @GetMapping(value = "/{tascaId}/responsables")
    public ResponseEntity<List<String>> getResponsablesV1(
    		@PathVariable("tascaId") Long tascaId) {
    	
        log.debug("[CTR] get responsables tasca: " + tascaId);

        List<String> responsables = tascaService.getResponsables(tascaId)
				.stream()
				.map(e -> e.getUsuariCodi())
				.collect(Collectors.toList());

        return new ResponseEntity<List<String>>(responsables, HttpStatus.OK);
    }
    
	@PostMapping(value = "/{tascaId}/responsables")
	public ResponseEntity<Void> setResponsablesV1(
			@PathVariable("tascaId") Long tascaId,
			@RequestParam(name = "responsables", required = false) List<String> responsables) {

		log.debug("[CTR] set responsables tasca: " + tascaId + "\n" + 
					"responsables: " + responsables);

        tascaService.setResponsables(tascaId, responsables);

		return new ResponseEntity<>(HttpStatus.OK);
    }
    
	@DeleteMapping(value = "/{tascaId}/responsables")
    public ResponseEntity<Void> deleteResponsablesV1(
    		@PathVariable("tascaId") Long tascaId) {
		
        log.debug("[CTR] delete responsables tasca: " + tascaId);
        
        tascaService.deleteResponsables(tascaId);
    	
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
