package es.caib.helium.client.domini.domini;

import com.fasterxml.jackson.databind.JsonNode;
import es.caib.helium.client.domini.domini.model.ConsultaDominisDades;
import es.caib.helium.client.domini.domini.model.ResultatDomini;
import es.caib.helium.client.domini.entorn.model.DominiDto;
import es.caib.helium.client.model.PagedList;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Map;

public interface DominiFeignClient {
	
	@RequestMapping(method = RequestMethod.GET, value = DominiApiPath.LIST_DOMINIS)
	public ResponseEntity<PagedList<DominiDto>> listDominisV1(
			@SpringQueryMap ConsultaDominisDades consultaDominisDades);
	

	@RequestMapping(method = RequestMethod.GET, value = DominiApiPath.CREATE_DOMINI)
	public ResponseEntity<Void> createDominiV1(@RequestBody DominiDto dominiDto);
	
	@RequestMapping(method = RequestMethod.PUT, value = DominiApiPath.UPDATE_DOMINI)
	public ResponseEntity<Void> updateDominiV1(
            @PathVariable("dominiId") Long dominiId,
            @Valid @RequestBody DominiDto dominiDto);
	
	@RequestMapping(method = RequestMethod.PATCH, value = DominiApiPath.UPDATE_DOMINI)
	public ResponseEntity<Void> patchDominiV1(
//          HttpServletRequest request,
          @PathVariable("dominiId") Long dominiId,
          @RequestBody JsonNode dominiJson);
	
	@RequestMapping(method = RequestMethod.DELETE, value = DominiApiPath.DELETE_DOMINI)
	public ResponseEntity<Void> deleteDominiV1(@PathVariable("dominiId") Long dominiId);
	
	@RequestMapping(method = RequestMethod.GET, value = DominiApiPath.GET_DOMINI)
	public ResponseEntity<DominiDto> getDominiV1(@PathVariable("dominiId") Long dominiId);

	@RequestMapping(method = RequestMethod.GET, value = DominiApiPath.CONSULTA_DOMINI)
	public ResponseEntity<ResultatDomini> consultaDominiV1(
            @PathVariable("dominiId") Long dominiId,
//            @RequestParam(value = "identificador", required = false) String identificador,
            @RequestParam(required = false) Map<String, String> parametres);
}
