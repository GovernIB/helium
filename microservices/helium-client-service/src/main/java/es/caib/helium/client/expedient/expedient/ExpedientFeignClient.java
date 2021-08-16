package es.caib.helium.client.expedient.expedient;

import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.JsonNode;

import es.caib.helium.client.expedient.expedient.model.ConsultaExpedientDades;
import es.caib.helium.client.expedient.expedient.model.ExpedientDto;
import es.caib.helium.client.model.PagedList;

public interface ExpedientFeignClient {

	@RequestMapping(method = RequestMethod.GET, value = ExpedientApiPath.FIND_EXPEDIENTS_AMB_FILTRE_PAGINAT)
	public ResponseEntity<PagedList<ExpedientDto>> findExpedientsAmbFiltrePaginatV1(
			@SpringQueryMap ConsultaExpedientDades consultaExpedientDades);

	@RequestMapping(method = RequestMethod.GET, value = ExpedientApiPath.FIND_EXPEDIENTS_IDS_AMB_FILTRE_PAGINAT)
	public ResponseEntity<PagedList<Long>> findExpedientsIdsAmbFiltrePaginatV1(
			@SpringQueryMap ConsultaExpedientDades consultaExpedientDades);

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
