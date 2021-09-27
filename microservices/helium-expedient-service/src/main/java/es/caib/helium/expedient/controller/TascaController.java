package es.caib.helium.expedient.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.helium.client.expedient.tasca.model.ConsultaTascaDades;
import es.caib.helium.expedient.model.TascaDto;
import es.caib.helium.expedient.service.TascaService;
import es.caib.helium.ms.controller.ControllerHelper;
import es.caib.helium.ms.model.PagedList;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    		ConsultaTascaDades consultaTascaDades ) {
    	
      Long entornId = consultaTascaDades.getEntornId();
      Long expedientTipusId = consultaTascaDades.getExpedientTipusId();
      String usuariAssignat = consultaTascaDades.getUsuariAssignat();
      List<String> grups = consultaTascaDades.getGrups();
      String nom = consultaTascaDades.getNom();
      String titol = consultaTascaDades.getTitol();
      Long expedientId = consultaTascaDades.getExpedientId();
      String expedientTitol = consultaTascaDades.getExpedientTitol();
      String expedientNumero = consultaTascaDades.getExpedientNumero();
      Date dataCreacioInici = consultaTascaDades.getDataCreacioInici();
      Date dataCreacioFi = consultaTascaDades.getDataCreacioFi();
      Date dataLimitInici = consultaTascaDades.getDataLimitInici();
      Date dataLimitFi = consultaTascaDades.getDataLimitFi();
      boolean mostrarAssignadesUsuari = consultaTascaDades.isMostrarAssignadesUsuari();
      boolean mostrarAssignadesGrup = consultaTascaDades.isMostrarAssignadesGrup();
      boolean nomesPendents = consultaTascaDades.isNomesPendents();
      String filtreRsql = consultaTascaDades.getFiltre();

    	log.debug("[CTR] llistant tasques: \n" +
                "entornId: " + entornId +
                ", expedientTipusId: " + expedientTipusId +
                ", usuariAssignat: " + usuariAssignat +
                ", grups: " + grups +
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
                "filtreRsql: " + filtreRsql);

        PagedList<TascaDto> tascaList = tascaService.listTasques(
        		entornId, 
        		expedientTipusId,
        		usuariAssignat, 
        		grups,
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
        		filtreRsql, 
        		consultaTascaDades.getPageable(), 
        		consultaTascaDades.getSort());
        if (tascaList.getTotalElements() == 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(tascaList, HttpStatus.OK);
    }

    /**
    *
	 * Consulta els identificadors de tasques amb paràmetres corresponent al
	 * llistat de tasques. La cerca pot rebre paràmetres per:
	 * <ul>
	 * <li>ordenar</li>
	 * <li>paginar</li>
	 * <li>filtrar (amb el mateixos paràmetres que en el llistat)</li>
	 * </ul>
    */
   @GetMapping(value = "/ids", produces = { "application/json" })
   public ResponseEntity<PagedList<String>> findTasquesIdsAmbFiltrePaginatV1(
   		ConsultaTascaDades consultaTascaDades ) {
   	
     Long entornId = consultaTascaDades.getEntornId();
     Long expedientTipusId = consultaTascaDades.getExpedientTipusId();
     String usuariAssignat = consultaTascaDades.getUsuariAssignat();
     List<String> grups = consultaTascaDades.getGrups();
     String nom = consultaTascaDades.getNom();
     String titol = consultaTascaDades.getTitol();
     Long expedientId = consultaTascaDades.getExpedientId();
     String expedientTitol = consultaTascaDades.getExpedientTitol();
     String expedientNumero = consultaTascaDades.getExpedientNumero();
     Date dataCreacioInici = consultaTascaDades.getDataCreacioInici();
     Date dataCreacioFi = consultaTascaDades.getDataCreacioFi();
     Date dataLimitInici = consultaTascaDades.getDataLimitInici();
     Date dataLimitFi = consultaTascaDades.getDataLimitFi();
     boolean mostrarAssignadesUsuari = consultaTascaDades.isMostrarAssignadesUsuari();
     boolean mostrarAssignadesGrup = consultaTascaDades.isMostrarAssignadesGrup();
     boolean nomesPendents = consultaTascaDades.isNomesPendents();
     String filtreRsql = consultaTascaDades.getFiltre();

   	log.debug("[CTR] llistant identificadors de tasques: \n" +
               "entornId: " + entornId +
               ", expedientTipusId: " + expedientTipusId +
               ", usuariAssignat: " + usuariAssignat +
               ", grups: " + grups +
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
               "filtreRsql: " + filtreRsql);

    //TODO: pensar en una consulta al servei que retorni identificadors
       PagedList<TascaDto> tascaList = tascaService.listTasques(
       		entornId, 
       		expedientTipusId,
       		usuariAssignat,
       		grups,
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
       		filtreRsql, 
       		consultaTascaDades.getPageable(), 
       		consultaTascaDades.getSort());
       if (tascaList.getTotalElements() == 0) {
           return new ResponseEntity<>(HttpStatus.NO_CONTENT);
       }
		List<String> tasquesIds = tascaList.getContent().stream().map(t -> t.getTascaId()).collect(Collectors.toList());
		PagedList<String> tasquesIdsPagedList = new PagedList<String>(tasquesIds);
		return new ResponseEntity<>(tasquesIdsPagedList, HttpStatus.OK);
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
            @PathVariable("tascaId") String tascaId,
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
            @PathVariable("tascaId") String tascaId,
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
        } 
        
        tascaService.updateTasca(
                tascaId,
                patchedTascaDto);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(value = "/{tascaId}")
    public ResponseEntity<Void> deleteTascaV1(
            @PathVariable("tascaId") String tascaId) {

        log.debug("[CTR] delete tasca: " + tascaId);

        tascaService.delete(tascaId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @GetMapping(value = "/{tascaId}")
    public ResponseEntity<TascaDto> getTascaV1(
            @PathVariable("tascaId") String tascaId) {

        log.debug("[CTR] get tasca: " + tascaId);
        return new ResponseEntity<>(tascaService.getById(tascaId), HttpStatus.OK);

    }
    
    
    /// Mètodes pels responsables
    
    @GetMapping(value = "/{tascaId}/responsables")
    public ResponseEntity<List<String>> getResponsablesV1(
    		@PathVariable("tascaId") String tascaId) {
    	
        log.debug("[CTR] get responsables tasca: " + tascaId);

        List<String> responsables = tascaService.getResponsables(tascaId);

        return new ResponseEntity<List<String>>(responsables, HttpStatus.OK);
    }
    
	@PostMapping(value = "/{tascaId}/responsables")
	public ResponseEntity<Void> setResponsablesV1(
			@PathVariable("tascaId") String tascaId,
			@RequestBody(required = false) List<String> responsables) {

		log.debug("[CTR] set responsables tasca: " + tascaId + "\n" + 
					"responsables: " + responsables);

        tascaService.setResponsables(tascaId, responsables);

		return new ResponseEntity<>(HttpStatus.OK);
    }
    
	@DeleteMapping(value = "/{tascaId}/responsables")
    public ResponseEntity<Void> deleteResponsablesV1(
    		@PathVariable("tascaId") String tascaId) {
		
        log.debug("[CTR] delete responsables tasca: " + tascaId);
        
        tascaService.deleteResponsables(tascaId);
    	
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
	
	
    /// Mètodes pels grups
    
    @GetMapping(value = "/{tascaId}/grups")
    public ResponseEntity<List<String>> getGrupsV1(
    		@PathVariable("tascaId") String tascaId) {
    	
        log.debug("[CTR] get grups tasca: " + tascaId);

        List<String> grups = tascaService.getGrups(tascaId);

        return new ResponseEntity<List<String>>(grups, HttpStatus.OK);
    }
    
	@PostMapping(value = "/{tascaId}/grups")
	public ResponseEntity<Void> setGrupsV1(
			@PathVariable("tascaId") String tascaId,
			@RequestBody(required = false) List<String> grups) {

		log.debug("[CTR] set grups tasca: " + tascaId + "\n" + 
					"grups: " + grups);

        tascaService.setGrups(tascaId, grups);

		return new ResponseEntity<>(HttpStatus.OK);
    }
    
	@DeleteMapping(value = "/{tascaId}/grups")
    public ResponseEntity<Void> deleteGrupsV1(
    		@PathVariable("tascaId") String tascaId) {
		
        log.debug("[CTR] delete grups tasca: " + tascaId);
        
        tascaService.deleteGrups(tascaId);
    	
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
