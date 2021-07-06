package es.caib.helium.client.domini.expedientTipus;

import java.util.Objects;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import es.caib.helium.client.domini.entorn.model.DominiDto;
import es.caib.helium.client.model.PagedList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExpedientTipusServiceImpl implements ExpedientTipusService {
	
	private final String missatgeLog = "Cridant Domini Service - ExpedientTipus - ";

	private ExpedientTipusFeignClient expedientTipusClient;

	@Override
	public PagedList llistaDominiByExpedientTipus(Long expedientTipusId, String filtreRsql, Pageable pageable, Sort sort) {

		log.debug(missatgeLog + " llista paginada de dominis segons expedientTipusId " + expedientTipusId + " amb filtre " + filtreRsql);
		var responseEntity = expedientTipusClient.llistaDominiByExpedientTipus(expedientTipusId, filtreRsql, pageable, sort);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
		return resultat;
	}

	@Override
	public DominiDto getDominiByExpedientTipusAndCodi(Long expedientTipusId, String codi, Long expedientTipusPareId) {
		
		log.debug(missatgeLog + " get dominis segons expedientTipusId " + expedientTipusId + " codi" + codi + " i expedientTipusPareId "+ expedientTipusPareId);
		var responseEntity = expedientTipusClient.getDominiByExpedientTipusAndCodi(expedientTipusId, codi, expedientTipusPareId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
		return resultat;
	}
}
