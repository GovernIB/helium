/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.core.model.hibernate.Domini;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioParametreDto;
import net.conselldemallorca.helium.v3.core.repository.DominiRepository;

/**
 * Mètodes per a la gestió d'integracions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class MonitorDominiHelper {

	private static final int MAX_ACCIONS_PER_DOMINI = 20;

	@Autowired
	private DominiRepository dominiRepository;

	private Map<Long, LinkedList<IntegracioAccioDto>> accionsDomini = new HashMap<Long, LinkedList<IntegracioAccioDto>>();
	private Map<Long, Integer> maxAccionsDomini = new HashMap<Long, Integer>();



	public List<DominiDto> findByEntorn(Entorn entorn) {
		List<Domini> dominis;
		if (entorn != null) {
			dominis = dominiRepository.findByEntorn(
					entorn);
		} else {
			dominis = dominiRepository.findAll();
		}
		List<DominiDto> dtos = new ArrayList<DominiDto>();
		for (Domini domini: dominis) {
			dtos.add(toDominiDto(domini));
		}
		return dtos;
	}

	public List<IntegracioAccioDto> findAccionsByDomini(
			Long dominiId) {
		return getLlistaAccions(dominiId);
	}

	public void addAccioOk(
			Domini domini,
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
			Domini domini,
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
			Domini domini,
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
		LinkedList<IntegracioAccioDto> accions = accionsDomini.get(dominiId);
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
		return accions;
	}
	private int getMaxAccions(
			Domini domini) {
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

	private void addAccio(
			Domini domini,
			String descripcio,
			IntegracioAccioTipusEnumDto tipus,
			IntegracioAccioEstatEnumDto estat,
			long tempsResposta,
			String errorDescripcio,
			Throwable throwable,
			IntegracioParametreDto ... parametres) {
		Long entornId = domini.getEntorn().getId();
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

	private DominiDto toDominiDto(Domini domini) {
		EntornDto entorn = new EntornDto();
		entorn.setId(domini.getEntorn().getId());
		entorn.setCodi(domini.getEntorn().getCodi());
		entorn.setNom(domini.getEntorn().getNom());
		DominiDto dto = new DominiDto(
				domini.getCodi(),
				domini.getNom(),
				entorn);
		dto.setId(domini.getId());
		LinkedList<IntegracioAccioDto> accions = accionsDomini.get(domini.getId());
		if (accions != null) {
			int numErrors = 0;
			for (IntegracioAccioDto accio: accions) {
				if (accio.isEstatError())
					numErrors++;
			}
			dto.setNumErrors(numErrors);
		}
		return dto;
	}

}
