/**
 * 
 */
package es.caib.helium.logic.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.helium.logic.intf.dto.DominiDto;
import es.caib.helium.logic.intf.dto.IntegracioAccioDto;
import es.caib.helium.logic.intf.dto.IntegracioAccioEstatEnumDto;
import es.caib.helium.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.helium.logic.intf.dto.IntegracioParametreDto;
import es.caib.helium.logic.ms.DominiMs;
import es.caib.helium.persist.entity.Entorn;

/**
 * Mètodes per a la gestió d'integracions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class MonitorDominiHelper {

	private static final int MAX_ACCIONS_PER_DOMINI = 20;

	@Autowired
	private DominiMs dominiMs;

	private Map<Long, LinkedList<IntegracioAccioDto>> accionsDomini = new HashMap<Long, LinkedList<IntegracioAccioDto>>();
	private Map<Long, Integer> maxAccionsDomini = new HashMap<Long, Integer>();



	public synchronized List<DominiDto> findByEntorn(Entorn entorn) {
		List<DominiDto> dominis;
		if (entorn != null) {
			dominis = dominiMs.llistaDominiByEntorn(entorn.getId());
		} else {
			dominis = new ArrayList<DominiDto>();
		}
		List<DominiDto> dtos = new ArrayList<DominiDto>();
		for (DominiDto domini: dominis) {
			dtos.add(toDominiDto(domini));
		}
		return dtos;
	}

	public synchronized List<IntegracioAccioDto> findAccionsByDomini(
			Long dominiId) {
		return getLlistaAccions(dominiId);
	}

	public void addAccioOk(
			DominiDto domini,
			String descripcio,
			IntegracioAccioTipusEnumDto tipus,
			long tempsResposta,
			IntegracioParametreDto ... parametres) {
		addAccio(
				domini,
				descripcio,
				tipus,
				IntegracioAccioEstatEnumDto.OK,
				tempsResposta,
				null,
				null,
				parametres);
	}
	public void addAccioError(
			DominiDto domini,
			String descripcio,
			IntegracioAccioTipusEnumDto tipus,
			long tempsResposta,
			String errorDescripcio,
			IntegracioParametreDto ... parametres) {
		addAccio(
				domini,
				descripcio,
				tipus,
				IntegracioAccioEstatEnumDto.ERROR,
				tempsResposta,
				errorDescripcio,
				null,
				parametres);
	}
	public void addAccioError(
			DominiDto domini,
			String descripcio,
			IntegracioAccioTipusEnumDto tipus,
			long tempsResposta,
			String errorDescripcio,
			Throwable throwable,
			IntegracioParametreDto ... parametres) {
		addAccio(
				domini,
				descripcio,
				tipus,
				IntegracioAccioEstatEnumDto.ERROR,
				tempsResposta,
				errorDescripcio,
				throwable,
				parametres);
	}


	private LinkedList<IntegracioAccioDto> getLlistaAccions(
			Long dominiId) {
		LinkedList<IntegracioAccioDto> accions = new LinkedList<IntegracioAccioDto>();
		try {
			accions = accionsDomini.get(dominiId);
			if (accions == null) {
				accions = new LinkedList<IntegracioAccioDto>();
				accionsDomini.put(
						dominiId,
						accions);
			} else {
				int index = 0;
				for (IntegracioAccioDto accio: accions) {
					accio.setIndex(new Long(index++));
				}
			}
		} catch (Exception ex) {
			logger.error("ERROR MONITOR DOMINI - GetLlistaAccions: ", ex);
		}
		return accions;
	}
	private int getMaxAccions(
			DominiDto domini) {
		Long dominiId = domini.getId();
		Integer max = maxAccionsDomini.get(dominiId);
		if (max == null) {
			max = new Integer(MAX_ACCIONS_PER_DOMINI);
			maxAccionsDomini.put(
					dominiId,
					max);
		}
		return max.intValue();
	}

	private synchronized void addAccio(
			DominiDto domini,
			String descripcio,
			IntegracioAccioTipusEnumDto tipus,
			IntegracioAccioEstatEnumDto estat,
			long tempsResposta,
			String errorDescripcio,
			Throwable throwable,
			IntegracioParametreDto ... parametres) {
		Long entornId = domini.getEntornId();
		IntegracioAccioDto accio = new IntegracioAccioDto();
		accio.setEntornId(entornId);
		accio.setIntegracioCodi(domini.getCodi());
		accio.setData(new Date());
		accio.setDescripcio(descripcio);
		accio.setTipus(tipus);
		accio.setEstat(estat);
		accio.setTempsResposta(tempsResposta);
		if (IntegracioAccioEstatEnumDto.ERROR.equals(estat)) {
			accio.setErrorDescripcio(errorDescripcio);
			if (throwable != null) {
				accio.setExcepcioMessage(
						ExceptionUtils.getMessage(throwable));
				accio.setExcepcioStacktrace(
						ExceptionUtils.getStackTrace(throwable));
			}
		}
		if (parametres != null) {
			accio.setParametres(
					new ArrayList<IntegracioParametreDto>(Arrays.asList(parametres)));
		}
		LinkedList<IntegracioAccioDto> accions = getLlistaAccions(domini.getId());
		int max = getMaxAccions(domini);
		while (accions.size() >= max) {
			accions.poll();
		}
		accions.add(accio);
	}

	private DominiDto toDominiDto(DominiDto domini) {
		LinkedList<IntegracioAccioDto> accions = accionsDomini.get(domini.getId());
		if (accions != null) {
			int numErrors = 0;
			for (IntegracioAccioDto accio: accions) {
				if (accio.isEstatError())
					numErrors++;
			}
			domini.setNumErrors(numErrors);
		}
		return domini;
	}

	private static final Logger logger = LoggerFactory.getLogger(MonitorDominiHelper.class);
}
