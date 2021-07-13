package es.caib.helium.client.expedient.expedient;

import java.util.Date;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import es.caib.helium.client.expedient.expedient.enums.ExpedientEstatTipusEnum;
import es.caib.helium.client.expedient.expedient.enums.MostrarAnulatsEnum;
import es.caib.helium.client.expedient.expedient.model.ExpedientDto;
import es.caib.helium.client.model.PagedList;

@Service
public interface ExpedientClientService {

	public PagedList<ExpedientDto> findExpedientsAmbFiltrePaginatV1(
	           Long entornId,
	           String filtre,
	           String usuariCodi,
	           Long expedientTipusId,
	           String titol,
	           String numero,
	           Date dataInici1,
	           Date dataInici2,
	           Date dataFi1,
	           Date dataFi2,
	           ExpedientEstatTipusEnum estatTipus,
	           Long estatId,
	           boolean nomesTasquesPersonals,
	           boolean nomesTasquesGrup,
	           boolean nomesAlertes,
	           boolean nomesErrors,
	           MostrarAnulatsEnum mostrarAnulats,
	           final Pageable pageable,	
	           final Sort sort);
	
	public void createExpedientV1(ExpedientDto expedientDto);
	
	public void updateExpedientV1(Long expedientId, ExpedientDto expedientDto);
	
	public void patchExpedientV1(Long expedientId, JsonNode expedientJson);
	
	public void deleteExpedientV1(Long expedientId);
	
	public ExpedientDto getExpedientV1(Long expedientId);
}
