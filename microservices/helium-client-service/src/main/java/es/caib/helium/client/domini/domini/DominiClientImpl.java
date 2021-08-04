package es.caib.helium.client.domini.domini;

import java.util.Map;
import java.util.Objects;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import es.caib.helium.client.domini.domini.model.ResultatDomini;
import es.caib.helium.client.domini.entorn.model.DominiDto;
import es.caib.helium.client.model.PagedList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class DominiClientImpl implements DominiClient {

	private final String missatgeLog = "Cridant Domini Service - DominiDomini - ";

	private DominiFeignClient dominiClient;

	@Override
	public PagedList<DominiDto> listDominisV1(Long entornId, String filtre, Long expedientTipusId,
			Long expedientTipusPareId, Pageable pageable, Sort sort) {
		
		log.debug(missatgeLog + " llista paginada de dominis segons entorn " + entornId 
				+ " de l'expedientTipusId " + expedientTipusId + " amb filtre " + filtre);
		var responseEntity = dominiClient.listDominisV1(entornId, filtre, expedientTipusId, expedientTipusPareId, pageable, sort);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
		return resultat;
	}

	@Override
	public Long createDominiV1(DominiDto dominiDto) {

		log.debug(missatgeLog + " creant domini amb codi " + dominiDto.getCodi() + " per l'entorn " + dominiDto.getEntorn());
		var responseEntity = dominiClient.createDominiV1(dominiDto);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
		return resultat;
	}

	@Override
	public void updateDominiV1(Long dominiId, DominiDto dominiDto) {
		
		log.debug(missatgeLog + " put domini amb id " + dominiId + " per l'entorn " + dominiDto.getEntorn());
		dominiClient.updateDominiV1(dominiId, dominiDto);
	}

	@Override
	public void patchDominiV1(Long dominiId, JsonNode dominiJson) {
		
		log.debug(missatgeLog + " patch domini amb id " + dominiId);
		dominiClient.patchDominiV1(dominiId, dominiJson);
	}

	@Override
	public void deleteDominiV1(Long dominiId) {
		
		log.debug(missatgeLog + " delete domini amb id " + dominiId);
		dominiClient.deleteDominiV1(dominiId);
	}

	@Override
	public DominiDto getDominiV1(Long dominiId) {
		
		log.debug(missatgeLog + " obtenir domini amb id " + dominiId);
		var responseEntity = dominiClient.getDominiV1(dominiId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
		return resultat;
	}

	@Override
	public ResultatDomini consultaDominiV1(Long dominiId, Map<String, String> parametres) {
		
		log.debug(missatgeLog + " consulta domini amb id " + dominiId + " parametres " + parametres.toString());
		var responseEntity = dominiClient.consultaDominiV1(dominiId, parametres);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
		return resultat;
	}
	
}
