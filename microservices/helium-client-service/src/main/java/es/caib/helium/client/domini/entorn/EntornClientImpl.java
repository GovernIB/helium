package es.caib.helium.client.domini.entorn;

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
public class EntornClientImpl implements EntornClient {
	
	private final String missatgeLog = "Cridant Domini Service - EntornDomini - ";

	private EntornFeignClient entornClient;
	
	@Override
	public PagedList listDominisByEntorn(Long entornId, String filtre, Pageable pageable, Sort sort) {
		
		log.debug(missatgeLog + " llistat paginat de dominis de l'entorn " + entornId + " amb filtre " + filtre);
		var responseEntity = entornClient.listDominisByEntorn(entornId, filtre, pageable, sort);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
		return resultat;
	}

	@Override
	public DominiDto getDominiByEntornAndCodi(Long entornId, String codi) {
	
		log.debug(missatgeLog + " llistat de dominis de l'entorn " + entornId + " amb codi " + codi);
		var responseEntity = entornClient.getDominiByEntornAndCodi(entornId, codi);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
		return resultat;
	}

}
