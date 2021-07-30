package es.caib.helium.client.domini.domini;

import com.fasterxml.jackson.databind.JsonNode;
import es.caib.helium.client.domini.domini.model.ConsultaDominisDades;
import es.caib.helium.client.domini.domini.model.ResultatDomini;
import es.caib.helium.client.domini.entorn.model.DominiDto;
import es.caib.helium.client.model.PagedList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class DominiClientImpl implements DominiClient {

	private final String missatgeLog = "Cridant Domini Service - DominiDomini - ";

	private final DominiFeignClient dominiFeignClient;

	@Override
	public PagedList<DominiDto> listDominisV1(ConsultaDominisDades consultaDominisDades) {
		
		log.debug(missatgeLog + " llista paginada de dominis segons entorn " + consultaDominisDades.getEntornId()
				+ " de l'expedientTipusId " + consultaDominisDades.getExpedientTipusId() + " amb filtre " + consultaDominisDades.getFiltre());
		var responseEntity = dominiFeignClient.listDominisV1(consultaDominisDades);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
		return resultat;
	}

	@Override
	public void createDominiV1(DominiDto dominiDto) {

		log.debug(missatgeLog + " creant domini amb codi " + dominiDto.getCodi() + " per l'entorn " + dominiDto.getEntorn());
		dominiFeignClient.createDominiV1(dominiDto);
	}

	@Override
	public void updateDominiV1(Long dominiId, DominiDto dominiDto) {
		
		log.debug(missatgeLog + " put domini amb id " + dominiId + " per l'entorn " + dominiDto.getEntorn());
		dominiFeignClient.updateDominiV1(dominiId, dominiDto);
	}

	@Override
	public void patchDominiV1(Long dominiId, JsonNode dominiJson) {
		
		log.debug(missatgeLog + " patch domini amb id " + dominiId);
		dominiFeignClient.patchDominiV1(dominiId, dominiJson);
	}

	@Override
	public void deleteDominiV1(Long dominiId) {
		
		log.debug(missatgeLog + " delete domini amb id " + dominiId);
		dominiFeignClient.deleteDominiV1(dominiId);
	}

	@Override
	public DominiDto getDominiV1(Long dominiId) {
		
		log.debug(missatgeLog + " obtenir domini amb id " + dominiId);
		var responseEntity = dominiFeignClient.getDominiV1(dominiId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
		return resultat;
	}

	@Override
	public ResultatDomini consultaDominiV1(Long dominiId, Map<String, String> parametres) {
		
		log.debug(missatgeLog + " consulta domini amb id " + dominiId + " parametres " + parametres.toString());
		var responseEntity = dominiFeignClient.consultaDominiV1(dominiId, parametres);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
		return resultat;
	}
	
}
