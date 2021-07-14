package es.caib.helium.client.expedient.tasca;

import com.fasterxml.jackson.databind.JsonNode;
import es.caib.helium.client.expedient.tasca.model.TascaDto;
import es.caib.helium.client.model.PagedList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class TascaClientServiceImpl implements TascaClientService {
	
	private final String MISSATGE_LOG = "Cridant Expedient Service - Tasca - ";
	
	private final TascaFeignClient tascaClient;

	@Override
	public PagedList<TascaDto> findTasquesAmbFiltrePaginatV1(Long entornId, Long expedientTipusId,
			String usuariAssignat, String nom, String titol, Long expedientId, String expedientTitol,
			String expedientNumero, Date dataCreacioInici, Date dataCreacioFi, Date dataLimitInici, Date dataLimitFi,
			boolean mostrarAssignadesUsuari, boolean mostrarAssignadesGrup, boolean nomesPendents, String filtre,
			Pageable pageable, Sort sort) {
		
		log.debug(MISSATGE_LOG + " filtrant tasques per l'entorn " + entornId);
		var responseEntity = tascaClient.findTasquesAmbFiltrePaginatV1(entornId, expedientTipusId,
				usuariAssignat, nom, titol, expedientId, expedientTitol, expedientNumero, dataCreacioInici, dataCreacioFi, 
				dataLimitInici, dataLimitFi, mostrarAssignadesUsuari, mostrarAssignadesGrup, nomesPendents, filtre, pageable, sort);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public void createTascaV1(TascaDto tascaDto) {

		log.debug(MISSATGE_LOG + " creant tasca " + tascaDto.getTitol() + " per l'expedient " + tascaDto.getExpedientId());
		tascaClient.createTascaV1(tascaDto);
	}

	@Override
	public void updateTascaV1(Long tascaId, TascaDto tascaDto) {

		log.debug(MISSATGE_LOG + " actualitzant tasca amb id " + tascaId + " per l'expedient " + tascaDto.getExpedientId());
		tascaClient.createTascaV1(tascaDto);
	}

	@Override
	public void patchTascaV1(Long tascaId, JsonNode tascaJson) {
		
		log.debug(MISSATGE_LOG + " patch tasca amb id " + tascaId);
		tascaClient.patchTascaV1(tascaId, tascaJson);
	}

	@Override
	public void deleteTascaV1(Long tascaId) {

		log.debug(MISSATGE_LOG + " esborrant tasca amb id " + tascaId);
		tascaClient.deleteTascaV1(tascaId);
	}

	@Override
	public TascaDto getTascaV1(Long tascaId) {

		log.debug(MISSATGE_LOG + " obtinguent tasca amb id " + tascaId);
		var responseEntity = tascaClient.getTascaV1(tascaId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public List<String> getResponsablesV1(Long tascaId) {
		
		log.debug(MISSATGE_LOG + " obtinguent responsables de la tasca amb id " + tascaId);
		var responseEntity = tascaClient.getResponsablesV1(tascaId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public void setResponsablesV1(Long tascaId, List<String> responsables) {
		
		log.debug(MISSATGE_LOG + " esborrant tasca amb id " + tascaId);
		tascaClient.deleteTascaV1(tascaId);
	}

	@Override
	public void deleteResponsablesV1(Long tascaId) {
		
		log.debug(MISSATGE_LOG + " esborrant tasca amb id " + tascaId);
		tascaClient.deleteTascaV1(tascaId);
	}

}
