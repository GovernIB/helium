package es.caib.helium.client.domini.domini;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import es.caib.helium.client.domini.domini.model.ConsultaDominisDades;
import es.caib.helium.client.domini.domini.model.ResultatDomini;
import es.caib.helium.client.domini.entorn.model.ConsultaDominiDada;
import es.caib.helium.client.domini.entorn.model.DominiDto;
import es.caib.helium.client.model.PagedList;

@Service
public interface DominiClient {

	public PagedList<DominiDto> listDominisV1(
			ConsultaDominisDades consultaDominisDades);

	public Long createDominiV1(DominiDto dominiDto);

	public void updateDominiV1(Long dominiId, DominiDto dominiDto);

	public void patchDominiV1(
//          HttpServletRequest request,
			Long dominiId, JsonNode dominiJson);

	public void deleteDominiV1(Long dominiId);

	public DominiDto getDominiV1(Long dominiId);

	public ResultatDomini consultaDominiV1(Long dominiId, 
			Map<String, String> parametres);

	public List<ResultatDomini> consultaDominisV1(List<ConsultaDominiDada> consultaDominiDades);

}
