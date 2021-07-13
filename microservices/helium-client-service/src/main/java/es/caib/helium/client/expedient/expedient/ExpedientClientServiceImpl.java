package es.caib.helium.client.expedient.expedient;

import java.util.Date;
import java.util.Objects;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import es.caib.helium.client.expedient.expedient.enums.ExpedientEstatTipusEnum;
import es.caib.helium.client.expedient.expedient.enums.MostrarAnulatsEnum;
import es.caib.helium.client.expedient.expedient.model.ExpedientDto;
import es.caib.helium.client.model.PagedList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExpedientClientServiceImpl implements ExpedientClientService {
	
	private final String missatgeLog = "Cridant Expedient Service - Expedient - ";
	
	private ExpedientFeignClient expedientClient;
	
	@Override
	public PagedList<ExpedientDto> findExpedientsAmbFiltrePaginatV1(Long entornId, String filtre, String usuariCodi,
			Long expedientTipusId, String titol, String numero, Date dataInici1, Date dataInici2, Date dataFi1,
			Date dataFi2, ExpedientEstatTipusEnum estatTipus, Long estatId, boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup, boolean nomesAlertes, boolean nomesErrors, MostrarAnulatsEnum mostrarAnulats,
			Pageable pageable, Sort sort) {

		log.debug(missatgeLog + " filtrant tasques per l'entorn " + entornId);
		var responseEntity = expedientClient.findExpedientsAmbFiltrePaginatV1(entornId, filtre, usuariCodi,
				expedientTipusId, titol, numero, dataInici1, dataInici2, dataFi1, dataFi2, estatTipus, estatId, 
				nomesTasquesPersonals, nomesTasquesGrup, nomesAlertes, nomesErrors, mostrarAnulats, pageable, sort);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public void createExpedientV1(ExpedientDto expedientDto) {

		log.debug(missatgeLog + " creant expedient " + expedientDto.getTitol() + " per l'entorn " + expedientDto.getEntornId());
		expedientClient.createExpedientV1(expedientDto);
	}

	@Override
	public void updateExpedientV1(Long expedientId, ExpedientDto expedientDto) {

		log.debug(missatgeLog + " update expedient amb id " + expedientId + " per l'entorn " + expedientDto.getEntornId());
		expedientClient.updateExpedientV1(expedientId, expedientDto);
	}

	@Override
	public void patchExpedientV1(Long expedientId, JsonNode expedientJson) {

		log.debug(missatgeLog + " patch expedient amb id " + expedientId);
		expedientClient.patchExpedientV1(expedientId, expedientJson);
	}

	@Override
	public void deleteExpedientV1(Long expedientId) {
		
		log.debug(missatgeLog + " delete expedient amb id " + expedientId);
		expedientClient.deleteExpedientV1(expedientId);
	}

	@Override
	public ExpedientDto getExpedientV1(Long expedientId) {
		
		log.debug(missatgeLog + " obtinguent expedient amb id " + expedientId);
		var responseEntity = expedientClient.getExpedientV1(expedientId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

}
