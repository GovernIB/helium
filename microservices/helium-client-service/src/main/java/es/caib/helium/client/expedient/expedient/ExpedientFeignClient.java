package es.caib.helium.client.expedient.expedient;

import com.fasterxml.jackson.databind.JsonNode;
import es.caib.helium.client.expedient.expedient.enums.ExpedientEstatTipusEnum;
import es.caib.helium.client.expedient.expedient.enums.MostrarAnulatsEnum;
import es.caib.helium.client.expedient.expedient.model.ExpedientDto;
import es.caib.helium.client.model.PagedList;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

public interface ExpedientFeignClient {

	//TODO: mirar d'encapsular per passar menys paràmetres
	@RequestMapping(method = RequestMethod.GET, value = ExpedientApiPath.FIND_EXPEDIENTS_AMB_FILTRE_PAGINAT)
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
			   @RequestParam(value = "pageable") final Pageable pageable,
			   @RequestParam(value = "sort") final Sort sort);
	
	@RequestMapping(method = RequestMethod.POST, value = ExpedientApiPath.CREATE_EXPEDIENT)
	public ResponseEntity<Void> createExpedientV1(@RequestBody ExpedientDto expedientDto);
	
	@RequestMapping(method = RequestMethod.PUT, value = ExpedientApiPath.PUT_EXPEDIENT)
	public ResponseEntity<Void> updateExpedientV1(
            @PathVariable("expedientId") Long expedientId,
            @RequestBody ExpedientDto expedientDto);
	
	@RequestMapping(method = RequestMethod.PATCH, value = ExpedientApiPath.PATCH_EXPEDIENT)
	public ResponseEntity<Void> patchExpedientV1(
//          HttpServletRequest request,
          @PathVariable("expedientId") Long expedientId,
          @RequestBody JsonNode expedientJson);
	
	@RequestMapping(method = RequestMethod.DELETE, value = ExpedientApiPath.DELETE_EXPEDIENT)
    public ResponseEntity<Void> deleteExpedientV1(
            @PathVariable("expedientId") Long expedientId);
	
	@RequestMapping(method = RequestMethod.GET, value = ExpedientApiPath.GET_EXPEDIENT)
	public ResponseEntity<ExpedientDto> getExpedientV1(
            @PathVariable("expedientId") Long expedientId);
}
