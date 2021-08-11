package es.caib.helium.client.expedient.expedient;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import es.caib.helium.client.expedient.expedient.model.ConsultaExpedientDades;
import es.caib.helium.client.expedient.expedient.model.ExpedientDto;
import es.caib.helium.client.model.PagedList;

@Service
public interface ExpedientClientService {

	public PagedList<ExpedientDto> findExpedientsAmbFiltrePaginatV1(
			ConsultaExpedientDades consultaExpedientDades);

	public PagedList<Long> findExpedientsIdsAmbFiltrePaginatV1(
			ConsultaExpedientDades consultaExpedientDades);

	public void createExpedientV1(ExpedientDto expedientDto);
	
	public void updateExpedientV1(Long expedientId, ExpedientDto expedientDto);
	
	public void patchExpedientV1(Long expedientId, JsonNode expedientJson);
	
	public void deleteExpedientV1(Long expedientId);
	
	public ExpedientDto getExpedientV1(Long expedientId);
}
