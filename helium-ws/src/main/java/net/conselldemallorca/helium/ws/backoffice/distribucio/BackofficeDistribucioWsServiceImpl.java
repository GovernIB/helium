package net.conselldemallorca.helium.ws.backoffice.distribucio;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import es.caib.distribucio.rest.client.integracio.domini.Estat;
import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.DistribucioHelper;
import net.conselldemallorca.helium.core.helper.MonitorIntegracioHelper;
import net.conselldemallorca.helium.core.helper.PluginHelper;
import net.conselldemallorca.helium.core.model.hibernate.Anotacio;
import net.conselldemallorca.helium.core.util.GlobalProperties;
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
		
		logger.info("Rebuda la comunicació de " + ids.size() + "anotacions de registre de Distribucio. Inici del processament.");
		
		monitorIntegracioHelper.addAccioOk(
				MonitorIntegracioHelper.INTCODI_DISTRIBUCIO, 
				"Rebuda petició de " + (ids != null ? ids.size() : "null") + " anotacions de registre de Distribucio", 
				IntegracioAccioTipusEnumDto.RECEPCIO,
				0, 
				new IntegracioParametreDto("ids", ToStringBuilder.reflectionToString(ids)));

		es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId idWs;
		List<Anotacio> anotacions;
		Anotacio anotacio;
		List<Long> idsAnotacionsReprocessar =  new ArrayList<Long>();
		// Si la petició ja existeix determina què fer en cas de cada estat
		es.caib.distribucio.rest.client.integracio.domini.Estat estatDistribucio = es.caib.distribucio.rest.client.integracio.domini.Estat.PENDENT;
		List<ComunicarEstat> comunicarEstats = new ArrayList<ComunicarEstat>();
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
					logger.info("Anotació " + id.getIndetificador() + " encuada com a pendent de consulta");
					
				} else {

					String msg = null;
					// Si ja existeix primer es mira si ja està processada.
					if (AnotacioEstatEnumDto.PROCESSADA.equals(anotacio.getEstat())) {
						// Comunica l'estat de processada a Distribucio
						estatDistribucio = es.caib.distribucio.rest.client.integracio.domini.Estat.PROCESSADA;
						msg = "La petició ja s'ha processat anteriorment.";
						if (anotacio.getExpedient() != null) {
							msg += " L'anotació ha estat processada a l'expedient " + anotacio.getExpedient().getIdentificador();
						}
						// guarda el missatge a enviar
						comunicarEstats.add(new ComunicarEstat(
								idWs, 
								estatDistribucio, 
								msg));

						// Mira si l'anotació està en un estat pendent de que es processi per Helium (comunicada amb reintents sense esgotar o pendent automàtic)
					} else if (this.anotacioPendentProcessHelium(anotacio)){
						// No fa res, el processament ja comunicarà el resultat
					} else {
						// Posa l'anotació com a comunicada per a que es torni a consultar i processar
						distribucioHelper.resetConsulta(anotacio.getId(), null);
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
		Thread thread = new ComunicarEstatsThread("Comunicar " + comunicarEstats.size(), comunicarEstats);
		thread.start();	
		logger.info("Fi del processament de comunicació de " + ids.size() + "anotacions de registre de Distribucio. Es comunicaran " + comunicarEstats.size());		
	}
	
	private class ComunicarEstatsThread extends Thread {
		private List<ComunicarEstat> comunicarEstats;
		
		public ComunicarEstatsThread(String name, List<ComunicarEstat> comunicarEstats) {
			super(name);
			this.comunicarEstats = comunicarEstats;
		}
		
		@Override
		public void run() {
			comunicarEstats(this.comunicarEstats);
		}	
	}
	
	/** Comprova si l'anotaicó està en un estat en què Helium la processarà automàticament:
	 * - En pendent automàtic.
	 * - Comunicada amb reintents pendents.
	 * @param anotacio
	 * @return
	 */
	private boolean anotacioPendentProcessHelium(Anotacio anotacio) {
		return AnotacioEstatEnumDto.PENDENT_AUTO.equals(anotacio.getEstat())
					|| (AnotacioEstatEnumDto.COMUNICADA.equals(anotacio.getEstat())
						&& anotacio.getConsultaIntents() < this.getMaxConsultaIntents());
	}

	private int getMaxConsultaIntents() {
		int maxConsultaIntents;
		String valStr = null;
		try {
			valStr = GlobalProperties.getInstance().getProperty("app.anotacions.pendents.comprovar.intents", "5");
			maxConsultaIntents = Integer.valueOf(valStr);
		} catch(Exception e) {
			maxConsultaIntents = 5;
			logger.error("Valor enter per la propietat app.anotacions.pendents.comprovar.intents no és correcte: \"" + valStr + "\". Es posarà per el valor " + maxConsultaIntents);
		}
		return maxConsultaIntents;
	}

	private void comunicarEstats(List<ComunicarEstat> comunicarEstats) {
		for(ComunicarEstat comunicarEstat: comunicarEstats) {
			try {
				logger.info("Comunicant l'estat " + comunicarEstat.getEstat() + " de l'anotació " + comunicarEstat.getIdWs().getIndetificador() + " a DISTRIBUCIO.");
				if(comunicarEstat.getEstat()!=null)
					// Comunica l'estat actual
					distribucioHelper.canviEstat(
							comunicarEstat.getIdWs(),
							comunicarEstat.getEstat(),
							comunicarEstat.getMissatge());
			} catch(Exception e) {
				logger.error("Error comunicant l'estat a Distribucio de la petició amb id : " + comunicarEstat.getIdWs() + ": " + comunicarEstat.getEstat(), comunicarEstat.getMissatge());
			}
		}
	}
	
	
	private class ComunicarEstat {
		private es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId idWs;
		private es.caib.distribucio.rest.client.integracio.domini.Estat estat;
		private String missatge;
		
		public ComunicarEstat(es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId idWs2,
				Estat estatDistribucio, String msg) {
			this.idWs=idWs2;
			this.estat=estatDistribucio;
			this.missatge=msg;
		}
		public es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId getIdWs() {
			return idWs;
		}
		public es.caib.distribucio.rest.client.integracio.domini.Estat getEstat() {
			return estat;
		}
		public String getMissatge() {
			return missatge;
		}	
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
