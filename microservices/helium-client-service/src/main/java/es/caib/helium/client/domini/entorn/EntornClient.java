package es.caib.helium.client.domini.entorn;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import es.caib.helium.client.domini.entorn.model.DominiDto;
import es.caib.helium.client.model.PagedList;

@Service
public interface EntornClient {

	public PagedList listDominisByEntorn(
			Long entornId, 
			String filtre, 
			final Pageable pageable,
            final Sort sort); 
	
	public DominiDto getDominiByEntornAndCodi(Long entornId, String codi);
}
