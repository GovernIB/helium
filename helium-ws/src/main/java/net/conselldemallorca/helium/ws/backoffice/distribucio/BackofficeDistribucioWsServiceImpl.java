package net.conselldemallorca.helium.ws.backoffice.distribucio;


import java.text.SimpleDateFormat;
import java.util.List;

import javax.jws.WebService;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.DistribucioHelper;
import net.conselldemallorca.helium.core.helper.MonitorIntegracioHelper;
import net.conselldemallorca.helium.core.model.hibernate.Anotacio;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioParametreDto;
import net.conselldemallorca.helium.v3.core.repository.AnotacioAnnexRepository;
import net.conselldemallorca.helium.v3.core.repository.AnotacioInteressatRepository;
import net.conselldemallorca.helium.v3.core.repository.AnotacioRepository;


/**
 * Implementació dels mètodes per al servei de backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
@WebService(
		name = "Backoffice",
		serviceName = "BackofficeService",
		portName = "BackofficeServicePort",
		targetNamespace = "http://www.caib.es/distribucio/ws/backoffice")
public class BackofficeDistribucioWsServiceImpl implements Backoffice {

	@Autowired
	private MonitorIntegracioHelper monitorIntegracioHelper;
	@Autowired
	private DistribucioHelper distribucioHelper;
	@Autowired
	private AnotacioRepository anotacioRepository;
	@Autowired
	private AnotacioAnnexRepository anotacioAnnexRepository;
	@Autowired
	private AnotacioInteressatRepository anotacioInteressatRepository;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	
	// Per donar format a les dates
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	/** Mètode invocat per Distribució per comunicar anotacions de registre al backoffice Helium. */
	@Override
	public synchronized void comunicarAnotacionsPendents(List<AnotacioRegistreId> ids) {
		
		logger.info("Rebuda petició d'anotacions de registre de Distribucio " + ids + ". Inici del processament.");
		monitorIntegracioHelper.addAccioOk(
				MonitorIntegracioHelper.INTCODI_DISTRIBUCIO, 
				"Rebuda petició de " + (ids != null ? ids.size() : "null") + " anotacions de registre de Distribucio", 
				IntegracioAccioTipusEnumDto.RECEPCIO,
				0, 
				new IntegracioParametreDto("ids", ToStringBuilder.reflectionToString(ids)));

		es.caib.distribucio.rest.client.domini.AnotacioRegistreId idWs;
		List<Anotacio> anotacions;
		Anotacio anotacio;
		for (AnotacioRegistreId id : ids) {
			idWs = conversioTipusHelper.convertir(id, es.caib.distribucio.rest.client.domini.AnotacioRegistreId.class);
			try {
				anotacio = null;
				logger.info("Processant la peticio d'anotació amb id " + id.getIndetificador());
				// Comprova si ja està a BBDD, si ja està comunica l'estat a distribució sense més processament
				anotacions = anotacioRepository.findByDistribucioIdAndDistribucioClauAcces(id.getIndetificador(), id.getClauAcces());
				if (!anotacions.isEmpty()) {
					if (anotacions.size() > 1)
						logger.warn("S'han trobat " + anotacions.size() + " peticions d'anotació per l'identificador de Distribucio " + id.getIndetificador());
					anotacio = anotacions.get(0);
				}
				if (anotacio == null) {
					// Guarda la informació mínima a la taula d'anotacions per a que la tasca en segon pla la consulti i processi
					distribucioHelper.encuarAnotacio(idWs);
				} else {
					// Si la petició ja existeix determina què fer en cas de cada estat
					es.caib.distribucio.rest.client.domini.Estat estat = es.caib.distribucio.rest.client.domini.Estat.PENDENT;
					String msg = null;
					switch(anotacio.getEstat()) {
					case PENDENT:
						if (anotacio.getExpedientTipus() == null 
							|| anotacio.getExpedientTipus().isDistribucioProcesAuto()) 
						{
							distribucioHelper.reprocessarAnotacio(anotacio.getId());
						} else {
							estat = es.caib.distribucio.rest.client.domini.Estat.REBUDA;
							msg = "La petició ja s'ha rebut anteriorment i està pendent de processar o rebutjar manualment";
						}
						break;
					case PROCESSADA:
						estat = es.caib.distribucio.rest.client.domini.Estat.PROCESSADA;
						msg = "La petició ja s'ha processat anteriorment.";
						if (anotacio.getExpedient() != null) {
							msg += " L'anotació ha estat processada a l'expedient " + anotacio.getExpedient().getIdentificador();
						}
						break;
					case REBUTJADA:
						estat = null;
						msg = "L'anotació \"" + anotacio.getIdentificador() + "\" s'havia rebutjat anteriorment " + 
								(anotacio.getDataProcessament() != null? "amb data " + sdf.format(anotacio.getDataProcessament()) : "") + 
								"i motiu: " + anotacio.getRebuigMotiu() + ". Es marca com a comunicada per tornar a consultar-la i processar-la.";
						logger.debug(msg);
						distribucioHelper.resetConsulta(anotacio.getId(), null);
						break;
					case COMUNICADA:
						estat = null;
						// Posa a 0 el número d'intents per a que es torni a processar pel thread de la tasca programada de consultar anotacions pendents
						distribucioHelper.updateConsulta(
								anotacio.getId(), 
								0, 
								anotacio.getConsultaError(), 
								anotacio.getConsultaData());
						break;
					case ERROR_PROCESSANT:
						distribucioHelper.reprocessarAnotacio(anotacio.getId());
						break;
					}
					if (estat != null) {
						// Comunica l'estat actual
						distribucioHelper.canviEstat(
								idWs, 
								estat,
								msg);
					}
				}
			} catch(Exception e) {
				logger.error("Error rebent la petició d'anotació de registre amb id=" + id.getIndetificador() + " : " + e.getMessage() + ". Es comunica l'error a Distribucio", e);
				try {
					distribucioHelper.canviEstat(
							idWs, 
							es.caib.distribucio.rest.client.domini.Estat.ERROR,
							"Error rebent l'anotació amb id " + id.getIndetificador() + ": " + e.getMessage());
				} catch(Exception ed) {
					logger.error("Error comunicant l'error de recepció a Distribucio de la petició amb id : " + id.getIndetificador() + ": " + ed.getMessage(), ed);
				}
			}
		}		
	}
	
	private static final Logger logger = LoggerFactory.getLogger(BackofficeDistribucioWsServiceImpl.class);
}
