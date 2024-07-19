/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.PeticioPinbal;
import net.conselldemallorca.helium.core.util.EntornActual;
import net.conselldemallorca.helium.integracio.plugins.pinbal.DadesConsultaPinbal;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioParametreDto;
import net.conselldemallorca.helium.v3.core.api.dto.PeticioPinbalEstatEnum;
import net.conselldemallorca.helium.v3.core.repository.PeticioPinbalRepository;

/**
 * Mètodes per a la gestió d'integracions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class MonitorIntegracioHelper {

	@Resource private PeticioPinbalRepository peticioPinbalRepository;	
	
	public static final int MAX_ACCIONS_PER_INTEGRACIO = 50;

	public static final String INTCODI_PERSONA = "PERSONA";
	public static final String INTCODI_SISTRA = "SISTRA";
	public static final String INTCODI_DISTRIBUCIO = "DISTRIBUCIO";
	public static final String INTCODI_PFIRMA = "PFIRMA";
	public static final String INTCODI_FIRMA = "FIRMA";
	public static final String INTCODI_CUSTODIA = "CUSTODIA";
	public static final String INTCODI_REGISTRE = "REGISTRE";
	public static final String INTCODI_GESDOC = "GESDOC";
	public static final String INTCODI_CONVDOC = "CONVDOC";
	public static final String INTCODI_FIRMA_SERV = "FIRMA_SERV";
	public static final String INTCODI_ARXIU = "ARXIU";
	public static final String INTCODI_NOTIB = "NOTIB";
	public static final String INTCODI_VALIDASIG = "VALIDASIG";
	public static final String INTCODI_PINBAL = "PINBAL";
	public static final String INTCODI_UNITATS = "UNITATS";
	public static final String INTCODI_PROCEDIMENT = "PROCEDIMENT";
	public static final String INTCODI_DADES_EXTERNES = "DADES EXTERNES";


	private Map<String, LinkedList<IntegracioAccioDto>> accionsIntegracio = new HashMap<String, LinkedList<IntegracioAccioDto>>();
	private Map<String, Integer> maxAccionsIntegracio = new HashMap<String, Integer>();


	public List<IntegracioDto> findAll() {
		List<IntegracioDto> integracions = this.getLlistaIntegracions();
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
	
	public List<IntegracioDto> findAllEntornActual() {
		List<IntegracioDto> integracions = this.getLlistaIntegracions();
		for (IntegracioDto integracio: integracions) {
			Long entornId = EntornActual.getEntornId();
			LinkedList<IntegracioAccioDto> accions = accionsIntegracio.get(integracio.getCodi());
			LinkedList<IntegracioAccioDto> accionsFiltrats = new LinkedList<IntegracioAccioDto>();
			if (accions != null) {
				int numErrors = 0;
				for (IntegracioAccioDto accio: accions) {
					if(accio.getEntornId() != null && accio.getEntornId().equals(entornId)) {
						accionsFiltrats.add(accio);
						continue;
					}
						
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
	
	public synchronized List<IntegracioAccioDto> findAccionsByIntegracioCodiEntornsAdmin(String integracioCodi, List<EntornDto> entonsAdmin) {
		LinkedList<IntegracioAccioDto> accionsFiltrats = new LinkedList<IntegracioAccioDto>();
		if (entonsAdmin!=null && entonsAdmin.size()>0) {
			
			List<Long> entonsAdminIds = new ArrayList<Long>();
			for (EntornDto entornDto: entonsAdmin)
				entonsAdminIds.add(entornDto.getId());
			
			for (IntegracioAccioDto accio: getLlistaAccions(integracioCodi))
				if(accio.getEntornId() != null && entonsAdminIds.contains(accio.getEntornId()))
					accionsFiltrats.add(accio);
		}
		return accionsFiltrats;
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

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void guardaPeticioPinbalAmbError(
			Expedient expedient,
			DadesConsultaPinbal dadesConsultaPinbal,
			Exception ex) {
		//Cream la petició pinbal per el historic de peticions
		PeticioPinbal peticio = new PeticioPinbal();
		peticio.setDataPeticio(Calendar.getInstance().getTime());
		peticio.setAsincrona(dadesConsultaPinbal.isAsincrona());
		peticio.setEstat(PeticioPinbalEstatEnum.ERROR);
		peticio.setExpedient(expedient);
		peticio.setTipus(expedient.getTipus());
		peticio.setEntorn(expedient.getTipus().getEntorn());
		peticio.setProcediment(dadesConsultaPinbal.getCodiProcediment());
		peticio.setUsuari(SecurityContextHolder.getContext().getAuthentication().getName());

//		String traca = ExceptionUtils.getStackTrace(ex);		
//		byte[] inputBytes = traca.getBytes(StandardCharsets.UTF_8);
//		if (inputBytes.length>4000) {
//			int cutOffIndex = 4000;
//			while (cutOffIndex > 0 && (inputBytes[cutOffIndex] & 0xC0) == 0x80) {
//				cutOffIndex--;
//			}
//			traca = new String(inputBytes, 0, cutOffIndex, StandardCharsets.UTF_8);
//		}
//		peticio.setErrorMsg(traca);

		peticio.setErrorMsg(ExceptionUtils.getStackTrace(ex));
		peticioPinbalRepository.save(peticio);
	}	

	private List<IntegracioDto> getLlistaIntegracions() {
		List<IntegracioDto> integracions = new ArrayList<IntegracioDto>();
		
		integracions.add(new IntegracioDto(INTCODI_ARXIU, "Arxiu digital"));
		integracions.add(new IntegracioDto(INTCODI_PERSONA, "SEYCON"));
		integracions.add(new IntegracioDto(INTCODI_FIRMA, "Firma digital"));
		integracions.add(new IntegracioDto(INTCODI_PFIRMA, "Portafib"));
		integracions.add(new IntegracioDto(INTCODI_CUSTODIA, "Custòdia"));
		integracions.add(new IntegracioDto(INTCODI_REGISTRE, "REGWEB"));
		integracions.add(new IntegracioDto(INTCODI_SISTRA, "SISTRA"));
		integracions.add(new IntegracioDto(INTCODI_DISTRIBUCIO, "DISTRIBUCIO"));
		integracions.add(new IntegracioDto(INTCODI_CONVDOC, "Conversió doc."));
		integracions.add(new IntegracioDto(INTCODI_FIRMA_SERV, "Firma serv."));
		integracions.add(new IntegracioDto(INTCODI_NOTIB, "Notificació"));
		integracions.add(new IntegracioDto(INTCODI_VALIDASIG, "Valida sign."));
		integracions.add(new IntegracioDto(INTCODI_PINBAL, "PINBAL"));
		integracions.add(new IntegracioDto(INTCODI_UNITATS, "Unitats"));
		integracions.add(new IntegracioDto(INTCODI_PROCEDIMENT, "ROLSAC"));
		integracions.add(new IntegracioDto(INTCODI_DADES_EXTERNES, "Dades externes"));
		return integracions;
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
		accio.setEntornId(EntornActual.getEntornId());
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
}
