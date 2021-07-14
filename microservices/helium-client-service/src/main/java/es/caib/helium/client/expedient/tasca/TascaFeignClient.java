package es.caib.helium.client.expedient.tasca;

import com.fasterxml.jackson.databind.JsonNode;
import es.caib.helium.client.expedient.tasca.model.TascaDto;
import es.caib.helium.client.model.PagedList;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

public interface TascaFeignClient {

	@RequestMapping(method = RequestMethod.GET, value = TascaApiPath.FIND_TASQUES_AMB_FILTRE_PAGINAT)
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
			@RequestParam(value = "pageable") final Pageable pageable,
			@RequestParam(value = "sort") final Sort sort);
	
	@RequestMapping(method = RequestMethod.POST, value = TascaApiPath.CREATE_TASCA)
	public ResponseEntity<Void> createTascaV1(
            @Valid @RequestBody TascaDto tascaDto);
	
	@RequestMapping(method = RequestMethod.POST, value = TascaApiPath.UPDATE_TASCA)
	public ResponseEntity<Void> updateTascaV1(
            @PathVariable("tascaId") Long tascaId,
            @Valid @RequestBody TascaDto tascaDto);
	
	@RequestMapping(method = RequestMethod.PATCH, value = TascaApiPath.PATCH_TASCA)
	public ResponseEntity<Void> patchTascaV1(
//          HttpServletRequest request,
          @PathVariable("tascaId") Long tascaId,
          @RequestBody JsonNode tascaJson);
	
	@RequestMapping(method = RequestMethod.DELETE, value = TascaApiPath.PATCH_TASCA)
	public ResponseEntity<Void> deleteTascaV1(
            @PathVariable("tascaId") Long tascaId); 
	
	@RequestMapping(method = RequestMethod.GET, value = TascaApiPath.GET_TASCA)
	public ResponseEntity<TascaDto> getTascaV1(
            @PathVariable("tascaId") Long tascaId); 
	
	@RequestMapping(method = RequestMethod.GET, value = TascaApiPath.GET_RESPONSABLES)
	public ResponseEntity<List<String>> getResponsablesV1(
    		@PathVariable("tascaId") Long tascaId);
	
	
	@RequestMapping(method = RequestMethod.POST, value = TascaApiPath.SET_RESPONSABLES)
	public ResponseEntity<Void> setResponsablesV1(
			@PathVariable("tascaId") Long tascaId,
			@RequestParam(name = "responsables", required = false) List<String> responsables);

	@RequestMapping(method = RequestMethod.DELETE, value = TascaApiPath.DELETE_RESPONSABLES)
	public ResponseEntity<Void> deleteResponsablesV1(
    		@PathVariable("tascaId") Long tascaId);
}
