package net.conselldemallorca.helium.ws.backoffice.distribucio;


import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.WebService;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.distribucio.backoffice.utils.arxiu.ArxiuPluginListener;
import es.caib.distribucio.backoffice.utils.arxiu.BackofficeArxiuUtils;
import es.caib.distribucio.backoffice.utils.arxiu.BackofficeArxiuUtilsImpl;
import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.DistribucioHelper;
import net.conselldemallorca.helium.core.helper.MonitorIntegracioHelper;
import net.conselldemallorca.helium.core.helper.PluginHelper;
import net.conselldemallorca.helium.core.model.hibernate.Anotacio;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioParametreDto;
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
public class BackofficeDistribucioWsServiceImpl implements Backoffice, ArxiuPluginListener {

	@Autowired
	private MonitorIntegracioHelper monitorIntegracioHelper;
	@Autowired
	private DistribucioHelper distribucioHelper;
	@Autowired
	private AnotacioRepository anotacioRepository;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PluginHelper pluginHelper;
	
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

		es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId idWs;
		List<Anotacio> anotacions;
		Anotacio anotacio;
		for (AnotacioRegistreId id : ids) {
			idWs = conversioTipusHelper.convertir(id, es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId.class);
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
					es.caib.distribucio.rest.client.integracio.domini.Estat estat = es.caib.distribucio.rest.client.integracio.domini.Estat.PENDENT;
					String msg = null;
					BackofficeArxiuUtils backofficeUtils = new BackofficeArxiuUtilsImpl(pluginHelper.getArxiuPlugin());
					backofficeUtils.setArxiuPluginListener(this);
					switch(anotacio.getEstat()) {
					case PENDENT:
					case ERROR_PROCESSANT:
						if (anotacio.getExpedientTipus() == null || anotacio.getExpedientTipus().isDistribucioProcesAuto()) {
							anotacio = distribucioHelper.reprocessarAnotacio(anotacio.getId(), backofficeUtils);
							estat = this.toAnotacioDistribucioEstat(anotacio.getEstat());
						} else {
							estat = es.caib.distribucio.rest.client.integracio.domini.Estat.REBUDA;
							msg = "La petició ja s'ha rebut anteriorment i està pendent de processar o rebutjar manualment";
						}
						break;
					case PROCESSADA:
						estat = es.caib.distribucio.rest.client.integracio.domini.Estat.PROCESSADA;
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
							es.caib.distribucio.rest.client.integracio.domini.Estat.ERROR,
							"Error rebent l'anotació amb id " + id.getIndetificador() + ": " + e.getMessage());
				} catch(Exception ed) {
					logger.error("Error comunicant l'error de recepció a Distribucio de la petició amb id : " + id.getIndetificador() + ": " + ed.getMessage(), ed);
				}
			}
		}		
	}
	
	private es.caib.distribucio.rest.client.integracio.domini.Estat toAnotacioDistribucioEstat(AnotacioEstatEnumDto estatAnotacioHelium) {
		if (estatAnotacioHelium!=null) {
			switch (estatAnotacioHelium) {
			case COMUNICADA:
				return es.caib.distribucio.rest.client.integracio.domini.Estat.REBUDA;
			case PENDENT:
				return es.caib.distribucio.rest.client.integracio.domini.Estat.PENDENT;
			case ERROR_PROCESSANT:
				return es.caib.distribucio.rest.client.integracio.domini.Estat.ERROR;
			case PROCESSADA:
				return es.caib.distribucio.rest.client.integracio.domini.Estat.PROCESSADA;
			case REBUTJADA:
				return es.caib.distribucio.rest.client.integracio.domini.Estat.REBUTJADA;
			}
		}
		return null;
	}
	
	/** Mètode per implementar la interfície {@link ArxiuPluginListener} de Distribució per rebre events de quan es crida l'Arxiu i afegir
	 * els logs al monitor d'integracions. 
	 * @param metode
	 * @param parametres
	 * @param correcte
	 * @param error
	 * @param e
	 * @param timeMs
	 */
	@Override
	public void event(String metode, Map<String, String> parametres, boolean correcte, String error, Exception e, long timeMs) {
		
		IntegracioParametreDto[] parametresMonitor = new IntegracioParametreDto[parametres.size()];
		int i = 0;
		for (String nom : parametres.keySet())
			parametresMonitor[i++] = new IntegracioParametreDto(nom, parametres.get(nom));
		
		if (correcte) {
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_ARXIU, 
					"Invocació al mètode del plugin d'Arxiu " + metode, 
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					timeMs, 
					parametresMonitor);
		} else {
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_ARXIU, 
					"Error invocant al mètode del plugin d'Arxiu " + metode, 
					IntegracioAccioTipusEnumDto.ENVIAMENT, 
					timeMs,
					error, 
					e, 
					parametresMonitor);	
		}
	}
	
	private static final Logger logger = LoggerFactory.getLogger(BackofficeDistribucioWsServiceImpl.class);
}
