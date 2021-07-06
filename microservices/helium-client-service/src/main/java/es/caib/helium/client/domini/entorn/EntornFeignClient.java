package es.caib.helium.client.domini.entorn;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import es.caib.helium.client.domini.entorn.model.DominiDto;
import es.caib.helium.client.model.PagedList;

public interface EntornFeignClient {

	@RequestMapping(method = RequestMethod.GET, value = EntornApiPath.LIST_DOMINIS_BY_ENTORN)
	public ResponseEntity<PagedList> listDominisByEntorn(
            @PathVariable("entornId") Long entornId,
            @RequestParam(value = "filtre", required = false) String filtre,
            final Pageable pageable,
            final Sort sort); 
	
	@RequestMapping(method = RequestMethod.GET, value = EntornApiPath.LIST_DOMINIS_BY_ENTORN_AND_CODI)
	public ResponseEntity<DominiDto> getDominiByEntornAndCodi(
            @PathVariable("entornId") Long entornId,
            @PathVariable("codi") String codi);
}
