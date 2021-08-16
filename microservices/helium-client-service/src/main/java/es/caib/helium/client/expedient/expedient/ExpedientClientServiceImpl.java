package es.caib.helium.client.expedient.expedient;

import java.util.Objects;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import es.caib.helium.client.expedient.expedient.model.ConsultaExpedientDades;
import es.caib.helium.client.expedient.expedient.model.ExpedientDto;
import es.caib.helium.client.model.PagedList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
@RequiredArgsConstructor
public class ExpedientClientServiceImpl implements ExpedientClientService {
	
	private final String MISSATGE_LOG = "Cridant Expedient Service - Expedient - ";
	
	private final ExpedientFeignClient expedientClient;

	@Override
	public PagedList<ExpedientDto> findExpedientsAmbFiltrePaginatV1(ConsultaExpedientDades consultaExpedientDades) {
		
		log.debug(MISSATGE_LOG + " llista paginada d'expedients segons el filtre = " +  ReflectionToStringBuilder.toString(consultaExpedientDades));
		var responseEntity = expedientClient.findExpedientsAmbFiltrePaginatV1(consultaExpedientDades);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
		return resultat;
	}
	
	@Override
	public PagedList<Long> findExpedientsIdsAmbFiltrePaginatV1(ConsultaExpedientDades consultaExpedientDades) {
		
		log.debug(MISSATGE_LOG + " llista paginada d'identificadors d'expedients segons entorn " + consultaExpedientDades.getEntornId()
				+ " i consultaExpedientDades = " +  ReflectionToStringBuilder.toString(consultaExpedientDades));
		var responseEntity = expedientClient.findExpedientsIdsAmbFiltrePaginatV1(consultaExpedientDades);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
		return resultat;
	}

	@Override
	public void createExpedientV1(ExpedientDto expedientDto) {

		log.debug(MISSATGE_LOG + " creant expedient " + expedientDto.getTitol() + " per l'entorn " + expedientDto.getEntornId());
		expedientClient.createExpedientV1(expedientDto);
	}

	@Override
	public void updateExpedientV1(Long expedientId, ExpedientDto expedientDto) {

		log.debug(MISSATGE_LOG + " update expedient amb id " + expedientId + " per l'entorn " + expedientDto.getEntornId());
		expedientClient.updateExpedientV1(expedientId, expedientDto);
	}

	@Override
	public void patchExpedientV1(Long expedientId, JsonNode expedientJson) {

		log.debug(MISSATGE_LOG + " patch expedient amb id " + expedientId);
		expedientClient.patchExpedientV1(expedientId, expedientJson);
	}

	@Override
	public void deleteExpedientV1(Long expedientId) {
		
		log.debug(MISSATGE_LOG + " delete expedient amb id " + expedientId);
		expedientClient.deleteExpedientV1(expedientId);
	}

	@Override
	public ExpedientDto getExpedientV1(Long expedientId) {
		
		log.debug(MISSATGE_LOG + " obtinguent expedient amb id " + expedientId);
		var responseEntity = expedientClient.getExpedientV1(expedientId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

}
