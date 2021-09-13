package es.caib.helium.client.expedient.tasca;

import java.util.List;

import javax.validation.Valid;

import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.JsonNode;

import es.caib.helium.client.expedient.tasca.model.ConsultaTascaDades;
import es.caib.helium.client.expedient.tasca.model.TascaDto;
import es.caib.helium.client.model.PagedList;

public interface TascaFeignClient {

	@RequestMapping(method = RequestMethod.GET, value = TascaApiPath.FIND_TASQUES_AMB_FILTRE_PAGINAT)
	public ResponseEntity<PagedList<TascaDto>> findTasquesAmbFiltrePaginatV1(
			@SpringQueryMap ConsultaTascaDades consultaTascaDades);

	@RequestMapping(method = RequestMethod.GET, value = TascaApiPath.FIND_TASQUES_IDS_AMB_FILTRE_PAGINAT)
	public ResponseEntity<PagedList<String>> findTasquesIdsAmbFiltrePaginatV1(
			@SpringQueryMap ConsultaTascaDades consultaTascaDades);

	
	@RequestMapping(method = RequestMethod.POST, value = TascaApiPath.CREATE_TASCA)
	public ResponseEntity<Void> createTascaV1(
            @Valid @RequestBody TascaDto tascaDto);
	
	@RequestMapping(method = RequestMethod.POST, value = TascaApiPath.UPDATE_TASCA)
	public ResponseEntity<Void> updateTascaV1(
            @PathVariable("tascaId") String tascaId,
            @Valid @RequestBody TascaDto tascaDto);
	
	@RequestMapping(method = RequestMethod.PATCH, value = TascaApiPath.PATCH_TASCA)
	public ResponseEntity<Void> patchTascaV1(
//          HttpServletRequest request,
          @PathVariable("tascaId") String tascaId,
          @RequestBody JsonNode tascaJson);
	
	@RequestMapping(method = RequestMethod.DELETE, value = TascaApiPath.PATCH_TASCA)
	public ResponseEntity<Void> deleteTascaV1(
            @PathVariable("tascaId") String tascaId); 
	
	@RequestMapping(method = RequestMethod.GET, value = TascaApiPath.GET_TASCA)
	public ResponseEntity<TascaDto> getTascaV1(
            @PathVariable("tascaId") String tascaId); 
	
	@RequestMapping(method = RequestMethod.GET, value = TascaApiPath.GET_RESPONSABLES)
	public ResponseEntity<List<String>> getResponsablesV1(
    		@PathVariable("tascaId") String tascaId);
	
	
	@RequestMapping(method = RequestMethod.POST, value = TascaApiPath.SET_RESPONSABLES)
	public ResponseEntity<Void> setResponsablesV1(
			@PathVariable("tascaId") String tascaId,
			@RequestParam(name = "responsables", required = false) List<String> responsables);

	@RequestMapping(method = RequestMethod.DELETE, value = TascaApiPath.DELETE_RESPONSABLES)
	public ResponseEntity<Void> deleteResponsablesV1(
    		@PathVariable("tascaId") String tascaId);

	@RequestMapping(method = RequestMethod.GET, value = TascaApiPath.GET_GRUPS)
	public ResponseEntity<List<String>> getGrupsV1(
    		@PathVariable("tascaId") String tascaId);
	
	
	@RequestMapping(method = RequestMethod.POST, value = TascaApiPath.SET_GRUPS)
	public ResponseEntity<Void> setGrupsV1(
			@PathVariable("tascaId") String tascaId,
			@RequestParam(name = "grups", required = false) List<String> grups);

	@RequestMapping(method = RequestMethod.DELETE, value = TascaApiPath.DELETE_GRUPS)
	public ResponseEntity<Void> deleteGrupsV1(
    		@PathVariable("tascaId") String tascaId);
}
