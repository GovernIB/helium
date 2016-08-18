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
import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioParametreDto;

/**
 * Mètodes per a la gestió d'integracions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class MonitorIntegracioHelper {

	public static final int MAX_ACCIONS_PER_INTEGRACIO = 20;

	public static final String INTCODI_PERSONA = "PERSONA";
	public static final String INTCODI_SISTRA = "SISTRA";
	public static final String INTCODI_PFIRMA = "PFIRMA";
	public static final String INTCODI_FIRMA = "FIRMA";
	public static final String INTCODI_CUSTODIA = "CUSTODIA";
	public static final String INTCODI_REGISTRE = "REGISTRE";
	public static final String INTCODI_GESDOC = "GESDOC";
	public static final String INTCODI_CONVDOC = "CONVDOC";
	public static final String INTCODI_PFIRMA_CB = "PFIRMA_CB";

	private Map<String, LinkedList<IntegracioAccioDto>> accionsIntegracio = new HashMap<String, LinkedList<IntegracioAccioDto>>();
	private Map<String, Integer> maxAccionsIntegracio = new HashMap<String, Integer>();



	public List<IntegracioDto> findAll() {
		List<IntegracioDto> integracions = new ArrayList<IntegracioDto>();
		integracions.add(
				novaIntegracio(
						INTCODI_PERSONA));
		integracions.add(
				novaIntegracio(
						INTCODI_FIRMA));
		integracions.add(
				novaIntegracio(
						INTCODI_PFIRMA));
		integracions.add(
				novaIntegracio(
						INTCODI_PFIRMA_CB));
		integracions.add(
				novaIntegracio(
						INTCODI_CUSTODIA));
		integracions.add(
				novaIntegracio(
						INTCODI_REGISTRE));
		integracions.add(
				novaIntegracio(
						INTCODI_SISTRA));
		/*integracions.add(
				novaIntegracio(
						INTCODI_GESDOC));*/
		integracions.add(
				novaIntegracio(
						INTCODI_CONVDOC));
		for (IntegracioDto integracio: integracions) {
			LinkedList<IntegracioAccioDto> accions = accionsIntegracio.get(integracio.getCodi());
			if (accions != null) {
				int numErrors = 0;
				for (IntegracioAccioDto accio: accions) {
					if (accio.isEstatError())
						numErrors++;
				}
				integracio.setNumErrors(numErrors);
			}
		}
		return integracions;
	}

	public synchronized List<IntegracioAccioDto> findAccionsByIntegracioCodi(
			String integracioCodi) {
		return getLlistaAccions(integracioCodi);
	}

	public void addAccioOk(
			String integracioCodi,
			String descripcio,
			IntegracioAccioTipusEnumDto tipus,
			long tempsResposta,
			IntegracioParametreDto ... parametres) {
		addAccio(
				integracioCodi,
				descripcio,
				tipus,
				IntegracioAccioEstatEnumDto.OK,
				tempsResposta,
				null,
				null,
				parametres);
	}
	public void addAccioError(
			String integracioCodi,
			String descripcio,
			IntegracioAccioTipusEnumDto tipus,
			long tempsResposta,
			String errorDescripcio,
			IntegracioParametreDto ... parametres) {
		addAccio(
				integracioCodi,
				descripcio,
				tipus,
				IntegracioAccioEstatEnumDto.ERROR,
				tempsResposta,
				errorDescripcio,
				null,
				parametres);
	}
	public void addAccioError(
			String integracioCodi,
			String descripcio,
			IntegracioAccioTipusEnumDto tipus,
			long tempsResposta,
			String errorDescripcio,
			Throwable throwable,
			IntegracioParametreDto ... parametres) {
		addAccio(
				integracioCodi,
				descripcio,
				tipus,
				IntegracioAccioEstatEnumDto.ERROR,
				tempsResposta,
				errorDescripcio,
				throwable,
				parametres);
	}



	private LinkedList<IntegracioAccioDto> getLlistaAccions(
			String integracioCodi) {
		LinkedList<IntegracioAccioDto> accions = accionsIntegracio.get(integracioCodi);
		if (accions == null) {
			accions = new LinkedList<IntegracioAccioDto>();
			accionsIntegracio.put(
					integracioCodi,
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
			String integracioCodi) {
		Integer max = maxAccionsIntegracio.get(integracioCodi);
		if (max == null) {
			max = new Integer(MAX_ACCIONS_PER_INTEGRACIO);
			maxAccionsIntegracio.put(
					integracioCodi,
					max);
		}
		return max.intValue();
	}

	private synchronized void addAccio(
			String integracioCodi,
			String descripcio,
			IntegracioAccioTipusEnumDto tipus,
			IntegracioAccioEstatEnumDto estat,
			long tempsResposta,
			String errorDescripcio,
			Throwable throwable,
			IntegracioParametreDto ... parametres) {
		IntegracioAccioDto accio = new IntegracioAccioDto();
		accio.setIntegracioCodi(integracioCodi);
		accio.setData(new Date());
		accio.setDescripcio(descripcio);
		accio.setTempsResposta(tempsResposta);
		accio.setTipus(tipus);
		accio.setEstat(estat);
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
		LinkedList<IntegracioAccioDto> accions = getLlistaAccions(integracioCodi);
		int max = getMaxAccions(integracioCodi);
		while (accions.size() >= max) {
			accions.poll();
		}
		accions.add(accio);
	}

	private IntegracioDto novaIntegracio(
			String codi) {
		IntegracioDto integracio = new IntegracioDto();
		integracio.setCodi(codi);
		if (INTCODI_REGISTRE.equals(codi)) {
			integracio.setDescripcio("REGWEB");
		} else if (INTCODI_FIRMA.equals(codi)) {
			integracio.setDescripcio("Firma digital");
		} else if (INTCODI_PFIRMA.equals(codi)) {
			integracio.setDescripcio("Portafib");
		} else if (INTCODI_CUSTODIA.equals(codi)) {
			integracio.setDescripcio("Custòdia");
		} else if (INTCODI_GESDOC.equals(codi)) {
			integracio.setDescripcio("Gestió doc.");
		} else if (INTCODI_CONVDOC.equals(codi)) {
			integracio.setDescripcio("Conversió doc.");
		} else if (INTCODI_PERSONA.equals(codi)) {
			integracio.setDescripcio("SEYCON");
		} else if (INTCODI_SISTRA.equals(codi)) {
			integracio.setDescripcio("SISTRA");
		} else if (INTCODI_PFIRMA_CB.equals(codi)) {
			integracio.setDescripcio("Portafib CB");
		}
		return integracio;
	}

}
