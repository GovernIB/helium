package es.caib.helium.client.domini.domini;

import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import es.caib.helium.client.domini.domini.model.ResultatDomini;
import es.caib.helium.client.domini.entorn.model.DominiDto;
import es.caib.helium.client.model.PagedList;

@Service
public interface DominiClient {

	public PagedList<DominiDto> listDominisV1(
			Long entornId,
			String filtre,
			Long expedientTipusId,
			Long expedientTipusPareId,
			final Pageable pageable, final Sort sort);

	public Long createDominiV1(DominiDto dominiDto);

	public void updateDominiV1(Long dominiId, DominiDto dominiDto);

	public void patchDominiV1(
//          HttpServletRequest request,
			Long dominiId, JsonNode dominiJson);

	public void deleteDominiV1(Long dominiId);

	public DominiDto getDominiV1(Long dominiId);

	public ResultatDomini consultaDominiV1(Long dominiId, 
//            @RequestParam(value = "identificador", required = false) String identificador,
			Map<String, String> parametres);

}
