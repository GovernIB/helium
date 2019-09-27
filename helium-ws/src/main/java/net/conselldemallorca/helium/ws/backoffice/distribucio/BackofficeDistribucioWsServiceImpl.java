package net.conselldemallorca.helium.ws.backoffice.distribucio;


import java.util.List;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.distribucio.ws.backofficeintegracio.AnotacioRegistreEntrada;
import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.DistribucioHelper;


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
	private DistribucioHelper distribucioHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	
	/** Mètode invocat per Distribució per comunicar anotacions de registre al backoffice Helium. */
	@Override
	public void comunicarAnotacionsPendents(List<AnotacioRegistreId> ids) {
		
		logger.info("Rebuda petició d'anotacions de registre de Distribucio " + ids + ". Inici del processament.");
		es.caib.distribucio.ws.backofficeintegracio.AnotacioRegistreId idWs;
		for (AnotacioRegistreId id : ids) {
			idWs = conversioTipusHelper.convertir(id, es.caib.distribucio.ws.backofficeintegracio.AnotacioRegistreId.class);
			try {
				//TODO: processar l'anotació:
				// Comprova si ja està a BBDD, si ja està comunica l'estat a distribució sense més processament
				// si la petició no està a BBDD consulta la petició i la guarda a BBDD
				AnotacioRegistreEntrada anotacio = distribucioHelper.getBackofficeIntegracioServicePort().consulta(idWs);
				// Guarda l'anotació
				distribucioHelper.guardarAnotacio(id.getIndetificador(), anotacio);
				// Notifica a Distribucio que s'ha rebut correctament
				distribucioHelper.getBackofficeIntegracioServicePort().canviEstat(
						idWs, 
						es.caib.distribucio.ws.backofficeintegracio.Estat.REBUDA,
						"Petició rebuda a Helium.");
				logger.info("Rebuda correctament la petició d'anotació de registre amb id=" + id);
			} catch(Exception e) {
				logger.error("Error rebent la petició d'anotació de registre amb id=" + id + " : " + e.getMessage() + ". Es comunica l'error a Distribucio", e);
				try {
					distribucioHelper.getBackofficeIntegracioServicePort().canviEstat(
							idWs, 
							es.caib.distribucio.ws.backofficeintegracio.Estat.ERROR,
							"Error rebent l'anotació amb id " + id + ": " + e.getMessage());
				} catch(Exception ed) {
					logger.error("Error comunicant l'error de recepció a Distribucio de la petició amb id : " + id + ": " + ed.getMessage(), ed);
				}
			}
		}		
	}
	
	private static final Logger logger = LoggerFactory.getLogger(BackofficeDistribucioWsServiceImpl.class);
}
