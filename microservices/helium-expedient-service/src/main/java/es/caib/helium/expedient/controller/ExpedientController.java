package es.caib.helium.expedient.controller;

import java.util.Collection;
import java.util.Date;
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

import es.caib.helium.client.expedient.expedient.model.ConsultaExpedientDades;
import es.caib.helium.expedient.model.ExpedientDto;
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
		   ConsultaExpedientDades consultaExpedientDades) {

	   Long entornId = consultaExpedientDades.getEntornId();
       String filtreRsql = consultaExpedientDades.getFiltreRsql();
       String usuariCodi = consultaExpedientDades.getActorId();
       Long expedientTipusId = consultaExpedientDades.getTipusId();
       Collection<Long> tipusIdPermesos = consultaExpedientDades.getTipusIdPermesos();
       String titol = consultaExpedientDades.getTitol();
       String numero = consultaExpedientDades.getNumero();
       Date dataInici1 = consultaExpedientDades.getDataCreacioInici();
       Date dataInici2 = consultaExpedientDades.getDataCreacioFi();
       Date dataFi1 = consultaExpedientDades.getDataFiInici();
       Date dataFi2 = consultaExpedientDades.getDataFiFi();
       boolean nomesIniciats = consultaExpedientDades.isNomesIniciats();
       boolean nomesFinalitzats = consultaExpedientDades.isNomesFinalitzats();
       Long estatId = consultaExpedientDades.getEstatId();
       boolean nomesTasquesPersonals = consultaExpedientDades.isNomesTasquesPersonals();
       boolean nomesTasquesMeves = consultaExpedientDades.isNomesTasquesMeves();
       boolean nomesTasquesGrup = consultaExpedientDades.isNomesTasquesGrup();
       boolean nomesAlertes = consultaExpedientDades.isNomesAlertes();
       boolean nomesErrors = consultaExpedientDades.isNomesErrors();
       boolean mostrarAnulats = consultaExpedientDades.isMostrarAnulats();
       boolean mostrarNomesAnulats = consultaExpedientDades.isMostrarNomesAnulats();
       
       log.debug("[CTR] llistant expedients: \n" +
    		   "usuariCodi: " + usuariCodi +
               ", entornId: " + entornId +
               ", expedientTipusId: " + expedientTipusId +
               ", tipusIdPermesos: " + tipusIdPermesos +
               ", titol: " + titol +
               ", numero: " + numero +
               ", dataInici1: " + dataInici1 +
               ", dataInici2: " + dataInici2 +
               ", dataFi1: " + dataFi1 +
               ", dataFi2: " + dataFi2 +
               ", nomesIniciats: " + nomesIniciats +
               ", nomesFinalitzats: " + nomesFinalitzats +
               ", estatId: " + estatId +
               ", nomesTasquesPersonals: " + nomesTasquesPersonals +
               ", nomesTasquesMeves: " + nomesTasquesMeves +
               ", nomesTasquesGrup: " + nomesTasquesGrup +
               ", expedientTipusId: " + nomesAlertes +
               ", nomesErrors: " + nomesErrors +
               ", mostrarAnulats: " + mostrarAnulats + 
               ", mostrarNomesAnulats: " + mostrarNomesAnulats + 
               ", filtre: " + filtreRsql +
               ", page: " + consultaExpedientDades.getPage() +
               ", size: " + consultaExpedientDades.getSize() +
               ", sort: " + consultaExpedientDades.getSort());

       PagedList<ExpedientDto> expedientList = expedientService.listExpedients(
    		   usuariCodi,
    		   entornId,
    		   expedientTipusId, 
    		   tipusIdPermesos,
    		   titol, 
    		   numero, 
    		   dataInici1, 
    		   dataInici2, 
    		   dataFi1, 
    		   dataFi2, 
               nomesIniciats,
               nomesFinalitzats,
    		   estatId, 
    		   nomesTasquesPersonals,
    		   nomesTasquesMeves,
    		   nomesTasquesGrup, 
    		   nomesAlertes, 
    		   nomesErrors, 
               mostrarAnulats,
               mostrarNomesAnulats,
    		   filtreRsql,
    		   consultaExpedientDades.getPageable(),
    		   consultaExpedientDades.getSort());
    		   
       if (expedientList.getTotalElements() == 0) {
           return new ResponseEntity<>(HttpStatus.NO_CONTENT);
       }
       
       return new ResponseEntity<>(expedientList, HttpStatus.OK);
   }
   
	/**
	 * Consulta els identificadors d'expedients amb paràmetres corresponent al
	 * llistat d'expedients. La cerca pot rebre paràmetres per:
	 * <ul>
	 * <li>ordenar</li>
	 * <li>paginar</li>
	 * <li>filtrar (amb el mateixos paràmetres que en el llistat)</li>
	 * </ul>
	 */
	@GetMapping(value = "/ids", produces = { "application/json" })
	public ResponseEntity<PagedList<Long>> findExpedientsIdsAmbFiltrePaginatV1(
			ConsultaExpedientDades consultaExpedientDades) {

		Long entornId = consultaExpedientDades.getEntornId();
		String filtreRsql = consultaExpedientDades.getFiltreRsql();
		String usuariCodi = consultaExpedientDades.getActorId();
		Long expedientTipusId = consultaExpedientDades.getTipusId();
	    Collection<Long> tipusIdPermesos = consultaExpedientDades.getTipusIdPermesos();
		String titol = consultaExpedientDades.getTitol();
		String numero = consultaExpedientDades.getNumero();
		Date dataInici1 = consultaExpedientDades.getDataCreacioInici();
		Date dataInici2 = consultaExpedientDades.getDataCreacioFi();
		Date dataFi1 = consultaExpedientDades.getDataFiInici();
		Date dataFi2 = consultaExpedientDades.getDataFiFi();
		boolean nomesIniciats = consultaExpedientDades.isNomesIniciats();
		boolean nomesFinalitzats = consultaExpedientDades.isNomesFinalitzats();
		Long estatId = consultaExpedientDades.getEstatId();
		boolean nomesTasquesPersonals = consultaExpedientDades.isNomesTasquesPersonals();
		boolean nomesTasquesMeves = consultaExpedientDades.isNomesTasquesMeves();
		boolean nomesTasquesGrup = consultaExpedientDades.isNomesTasquesGrup();
		boolean nomesAlertes = consultaExpedientDades.isNomesAlertes();
		boolean nomesErrors = consultaExpedientDades.isNomesErrors();
		boolean mostrarAnulats = consultaExpedientDades.isMostrarAnulats();
		boolean mostrarNomesAnulats = consultaExpedientDades.isMostrarNomesAnulats();
      
      log.debug("[CTR] llistant identificadors d' expedients: \n" +
   		   "usuariCodi: " + usuariCodi +
              ", entornId: " + entornId +
              ", expedientTipusId: " + expedientTipusId +
              ", tipusIdPermesos: " + tipusIdPermesos +
              ", titol: " + titol +
              ", numero: " + numero +
              ", dataInici1: " + dataInici1 +
              ", dataInici2: " + dataInici2 +
              ", dataFi1: " + dataFi1 +
              ", dataFi2: " + dataFi2 +
              ", nomesIniciats: " + nomesIniciats +
              ", nomesFinalitzats: " + nomesFinalitzats +
              ", estatId: " + estatId +
              ", nomesTasquesPersonals: " + nomesTasquesPersonals +
              ", nomesTasquesMeves: " + nomesTasquesMeves +
              ", nomesTasquesGrup: " + nomesTasquesGrup +
              ", expedientTipusId: " + nomesAlertes +
              ", nomesErrors: " + nomesErrors +
              ", mostrarAnulats: " + mostrarAnulats + 
              ", mostrarNomesAnulats: " + mostrarNomesAnulats + 
              ", filtre: " + filtreRsql +
              ", page: " + consultaExpedientDades.getPage() +
              ", size: " + consultaExpedientDades.getSize() +
              ", sort: " + consultaExpedientDades.getSort());

      //TODO: pensar en una consulta al servei que retorni identificadors
      PagedList<ExpedientDto> expedientList = expedientService.listExpedients(
   		   usuariCodi,
   		   entornId,
   		   expedientTipusId, 
   		   tipusIdPermesos,
   		   titol, 
   		   numero, 
   		   dataInici1, 
   		   dataInici2, 
   		   dataFi1, 
   		   dataFi2, 
           nomesIniciats,
           nomesFinalitzats,
   		   estatId, 
   		   nomesTasquesPersonals, 
   		   nomesTasquesMeves, 
   		   nomesTasquesGrup, 
   		   nomesAlertes, 
   		   nomesErrors, 
           mostrarAnulats,
           mostrarNomesAnulats,
   		   filtreRsql,
   		   consultaExpedientDades.getPageable(),
   		   consultaExpedientDades.getSort());
   		   
		if (expedientList.getTotalElements() == 0) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		List<Long> expedientsIds = expedientList.getContent().stream().map(e -> e.getId()).collect(Collectors.toList());
		PagedList<Long> expedientIdsPagedList = new PagedList<Long>(expedientsIds);
		return new ResponseEntity<>(expedientIdsPagedList, HttpStatus.OK);
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
        } 
        expedientService.updateExpedient(
                expedientId,
                patchedExpedientDto);

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
    
    /** Actualitza l'expedient amb les dades per aturar-lo. 
     */
    @PostMapping(value = "/{expedientId}/aturar", consumes = { "application/json" })
    public ResponseEntity<Void> aturar(
            @PathVariable("expedientId") Long expedientId,
            @Valid @RequestBody String comentari) {

        log.debug("[CTR] aturar expedient: " + expedientId);

        try {
        	ExpedientDto expedientDto = expedientService.getById(expedientId);
        	if (expedientDto == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        	expedientDto.setAturat(true);
        	expedientDto.setInfoAturat(comentari);
            expedientService.updateExpedient(
                    expedientId,
                    expedientDto);
        } catch (DataIntegrityViolationException ex) {
        	String errMsg = "Error aturant l'expedient " + expedientId + ": " + ex.getMessage();
            log.error("[CTR] Aturar: " + errMsg, ex);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errMsg);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /** Reprén l'expedient. 
     */
    @PostMapping(value = "/{expedientId}/reprendre", consumes = { "application/json" })
    public ResponseEntity<Void> reprendre(
            @PathVariable("expedientId") Long expedientId) {

        log.debug("[CTR] reprendre expedient: " + expedientId);

        try {
        	ExpedientDto expedientDto = expedientService.getById(expedientId);
        	if (expedientDto == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        	expedientDto.setAturat(false);
        	expedientDto.setInfoAturat(null);
            expedientService.updateExpedient(
                    expedientId,
                    expedientDto);
        } catch (DataIntegrityViolationException ex) {
        	String errMsg = "Error reprenent l'expedient " + expedientId + ": " + ex.getMessage();
            log.error("[CTR] Reprendre: " + errMsg, ex);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errMsg);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /** Actualitza l'expedient amb les dades d'anul·lació. 
     */
    @PostMapping(value = "/{expedientId}/anular", consumes = { "application/json" })
    public ResponseEntity<Void> anular(
            @PathVariable("expedientId") Long expedientId,
            @Valid @RequestBody String comentari) {

        log.debug("[CTR] anular expedient: " + expedientId);

        try {
        	ExpedientDto expedientDto = expedientService.getById(expedientId);
        	if (expedientDto == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        	expedientDto.setAnulat(true);
        	expedientDto.setComentariAnulat(comentari);
            expedientService.updateExpedient(
                    expedientId,
                    expedientDto);
        } catch (DataIntegrityViolationException ex) {
        	String errMsg = "Error anul·lant l'expedient " + expedientId + ": " + ex.getMessage();
            log.error("[CTR] Anular: " + errMsg, ex);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errMsg);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /** Desanul·la l'expedient. 
     */
    @PostMapping(value = "/{expedientId}/desanular", consumes = { "application/json" })
    public ResponseEntity<Void> desanular(
            @PathVariable("expedientId") Long expedientId) {

        log.debug("[CTR] desanular expedient: " + expedientId);

        try {
        	ExpedientDto expedientDto = expedientService.getById(expedientId);
        	if (expedientDto == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        	expedientDto.setAnulat(false);
        	expedientDto.setComentariAnulat(null);
            expedientService.updateExpedient(
                    expedientId,
                    expedientDto);
        } catch (DataIntegrityViolationException ex) {
        	String errMsg = "Error desanul·lant l'expedient " + expedientId + ": " + ex.getMessage();
            log.error("[CTR] Desanular: " + errMsg, ex);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errMsg);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
