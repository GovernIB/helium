package es.caib.helium.client.domini.expedientTipus;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import es.caib.helium.client.domini.entorn.model.DominiDto;
import es.caib.helium.client.model.PagedList;


public interface ExpedientTipusFeignClient {
	
	@RequestMapping(method = RequestMethod.GET, value = ExpedientTipusApiPath.LLISTA_DOMINI_BY_EXPDIENT_TIPUS)
	public ResponseEntity<PagedList> llistaDominiByExpedientTipus(
            @PathVariable("expedientTipusId") Long expedientTipusId,
            @RequestParam(value = "filtre", required = false) String filtreRsql,
            final Pageable pageable,
            final Sort sort);
	
	@RequestMapping(method = RequestMethod.GET, value = ExpedientTipusApiPath.GET_DOMINI_BY_EXPDIENT_TIPUS_AND_CODI)
	ResponseEntity<DominiDto> getDominiByExpedientTipusAndCodi(
            @PathVariable("expedientTipusId") Long expedientTipusId,
            @PathVariable("codi") String codi,
            @RequestParam(value = "expedientTipusPareId", required = false) Long expedientTipusPareId); 
}
