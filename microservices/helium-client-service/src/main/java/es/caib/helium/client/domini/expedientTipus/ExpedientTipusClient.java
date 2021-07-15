package es.caib.helium.client.domini.expedientTipus;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import es.caib.helium.client.domini.entorn.model.DominiDto;
import es.caib.helium.client.model.PagedList;

@Service
public interface ExpedientTipusClient {

	public PagedList llistaDominiByExpedientTipus(Long expedientTipusId, String filtreRsql, final Pageable pageable, final Sort sort);
	
	public DominiDto getDominiByExpedientTipusAndCodi(Long expedientTipusId, String codi, Long expedientTipusPareId); 
}
