package es.caib.helium.expedient.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.webmvc.json.patch.JsonPatchPatchConverter;
import org.springframework.data.rest.webmvc.json.patch.Patch;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.helium.client.expedient.proces.model.ConsultaProcesDades;
import es.caib.helium.expedient.model.ProcesDto;
import es.caib.helium.expedient.service.ProcesService;
import es.caib.helium.ms.controller.ControllerHelper;
import es.caib.helium.ms.model.PagedList;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador que defineix la API REST de processos per mantenir informació
 * de l'arbre de processos per expedients i tasques i poder realitzar consultes
 * específiques.
 */
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(ProcesController.API_PATH)
public class ProcesController {

	public static final String API_PATH = "/api/v1/processos";

	private final ProcesService procesService;
	private final ObjectMapper objectMapper;
	private final SmartValidator smartValidator;

	/** Cerca de processos filtrada i paginada
	 *
	 * @param filtre   Filtre a aplicar en la consulta. El filtre té format RSQL.
	 *                 {@see https://github.com/jirutka/rsql-parser}. Exemple:
	 *                 {@code nom=ic=*tasca*,codi==T1}
	 * @param pageable Informació de paginació. Exemple: {@code page=0&size=25}
	 * @param sort     Informació de ordre Exemple: {@code nom,desc}
	 * @return Retorna una pàgina del llistat de tasques, un cop aplicats els camps
	 *         de filtre. <br/>
	 *         La cerca pot rebre paràmetres per:
	 *         <ul>
	 *         <li>ordenar</li>
	 *         <li>paginar</li>
	 *         <li>filtrar (utilitzant sintaxi rsql)</li>
	 *         </ul>
	 */
	@GetMapping(produces = { "application/json" })
   public ResponseEntity<PagedList<ProcesDto>> findProcesosAmbFiltrePaginatV1(
		   ConsultaProcesDades consultaProcesDades ) {

   	log.debug("[CTR] llistant processos: \n" + consultaProcesDades);

       PagedList<ProcesDto> procesList = procesService.listProcessos(
       		consultaProcesDades.getProcessDefinitionId(), 
       		consultaProcesDades.getProcesArrelId(), 
       		consultaProcesDades.getFiltreRsql(),
       		consultaProcesDades.getPageable(), 
       		consultaProcesDades.getSort());
       if (procesList.getTotalElements() == 0) {
           return new ResponseEntity<>(HttpStatus.NO_CONTENT);
       }
       return new ResponseEntity<>(procesList, HttpStatus.OK);
   }

   /**
   *
	 * Cerca d'identificadors de processos filtrada i paginada. 
	 * La cerca pot rebre paràmetres per:
	 * <ul>
	 * <li>ordenar</li>
	 * <li>paginar</li>
	 * <li>filtrar (amb el mateixos paràmetres que en el llistat)</li>
	 * </ul>
   */
  @GetMapping(value = "/ids", produces = { "application/json" })
  public ResponseEntity<PagedList<String>> findProcessosIdsAmbFiltrePaginatV1(
		  ConsultaProcesDades consultaProcesDades ) {
  	

  	log.debug("[CTR] llistant identificadors de tasques: \n" + consultaProcesDades);

   //TODO: pensar en una consulta al servei que retorni identificadors
      PagedList<ProcesDto> tascaList = procesService.listProcessos(
    		  consultaProcesDades.getProcessDefinitionId(),
    		  consultaProcesDades.getProcesArrelId(),
    		  consultaProcesDades.getFiltreRsql(),
    		  consultaProcesDades.getPageable(), 
    		  consultaProcesDades.getSort());

		List<String> processosIds = tascaList.getContent().stream().map(t -> t.getProcesId()).collect(Collectors.toList());
		PagedList<String> processosIdsPagedList = new PagedList<String>(processosIds);
		return new ResponseEntity<>(processosIdsPagedList, HttpStatus.OK);
  }
  
  
    /** Crea una entrada a la taula de processos */
    @PostMapping(consumes = { "application/json" })
    public ResponseEntity<Void> createProcesV1(
            @Valid @RequestBody ProcesDto procesDto) {

        log.debug("[CTR] create proces: " + procesDto.toString());
        ProcesDto savedDto;

        try {
            savedDto = procesService.createProces(procesDto);
        } catch (DataIntegrityViolationException ex) {
            log.error("[CTR] Create: Ja existeix un proces amb el mateix entorn, tipus d'proces i codi", ex);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ja existeix un domini amb el mateix entorn, tipus d'proces i codi");
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Location", API_PATH + "/" + savedDto.getId());
        return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);

    }

    @PutMapping(value = "/{procesId}", consumes = { "application/json" })
    public ResponseEntity<Void> updateProcesV1(
            @PathVariable("procesId") String procesId,
            @Valid @RequestBody ProcesDto procesDto) {

        log.debug("[CTR] update proces: " + procesDto.toString());

        procesService.updateProces(
                procesId,
                procesDto);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @PatchMapping(value = "/{procesId}", consumes = { "application/json" })
    public ResponseEntity<Void> patchProcesV1(
//            HttpServletRequest request,
            @PathVariable("procesId") String procesId,
            @RequestBody JsonNode procesJson,
            BindingResult bindingResult) {

        log.debug("[CTR] patch proces: " + procesId);

        Patch patch = new JsonPatchPatchConverter(objectMapper).convert(procesJson);
        ProcesDto procesDto = procesService.getById(procesId);
        ProcesDto patchedProcesDto = patch.apply(
                procesDto,
                ProcesDto.class
        );
        smartValidator.validate(patchedProcesDto, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ControllerHelper.getValidationErrorMessage(bindingResult));
        } 
        
        procesService.updateProces(
                procesId,
                patchedProcesDto);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(value = "/{procesId}")
    public ResponseEntity<Void> deleteProcesV1(
            @PathVariable("procesId") String procesId) {

        log.debug("[CTR] delete proces: " + procesId);

        procesService.delete(procesId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @GetMapping(value = "/{procesId}")
    public ResponseEntity<ProcesDto> getProcesV1(
            @PathVariable("procesId") String procesId) {

        log.debug("[CTR] get proces: " + procesId);
        return new ResponseEntity<>(procesService.getById(procesId), HttpStatus.OK);
    }
    
    /** Consulta el llistat de tots els processos que pengen a partir del procés arrel del procés
     * passat com a argument.
     *  
     * @param procesId	Identificador de la instància de procés a partir del qual s'obté el procés arrel per
     * consultar la resta de procéssos que tenen el mateix procés arrel.
     * 
     * @return Retorna la llista de processos que tenen el mateix procés arrel que el procés passat com
     * a paràmetre.
     */
    @GetMapping(value = "/{procesId}/llistat")
    public ResponseEntity<List<ProcesDto>> getLlistatProcessosV1(
            @PathVariable("procesId") String procesId) {

        log.debug("[CTR] get llistat processos: " + procesId);
        
		String procesArrelId = procesService.getById(procesId).getProcesArrelId();
		List<ProcesDto> processos = procesService.listProcessos(null, procesArrelId, null, null, null).getContent();
        
        return new ResponseEntity<>(processos, HttpStatus.OK);
    }

    /** Consulta l'identificador de l'expedient a partir de l'identificador de la instància de procés.
     *  
     * @param procesId	Identificador de la instància de procés a partir del qual s'obté el procés arrel per
     * consultar la resta de procéssos que tenen el mateix procés arrel.
     * 
     * @return Retorna únicament l'identificador de l'expedient de la instància de procés.
     */
    @GetMapping(value = "/{procesId}/expedientId")
    public ResponseEntity<Long> getExpedientIdV1(
            @PathVariable("procesId") String procesId) {

        log.debug("[CTR] get expedientId proces: " + procesId);
        
        ProcesDto proces = procesService.getById(procesId);
        if (proces == null )
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Instància de procés: " + procesId);
        
        return new ResponseEntity<>(proces.getExpedientId(), HttpStatus.OK);
    }

    
}
