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

		es.caib.distribucio.ws.backofficeintegracio.AnotacioRegistreId idWs;
		List<Anotacio> anotacions;
		Anotacio anotacio;
		for (AnotacioRegistreId id : ids) {
			idWs = conversioTipusHelper.convertir(id, es.caib.distribucio.ws.backofficeintegracio.AnotacioRegistreId.class);
			try {
				anotacio = null;
				logger.info("Processant la peticio d'anotació amb id " + id.getIndetificador());
				// Comprova si ja està a BBDD, si ja està comunica l'estat a distribució sense més processament
				anotacions = anotacioRepository.findByDistribucioId(id.getIndetificador());
				if (!anotacions.isEmpty()) {
					if (anotacions.size() > 1)
						logger.warn("S'han trobat " + anotacions.size() + " peticions d'anotació per l'identificador de Distribucio " + id.getIndetificador());
					anotacio = anotacions.get(0);
				}
				if (anotacio == null) {
					
					distribucioHelper.processarAnotacio(idWs);
					
					/*
					// begin transaction
					
					// si la petició no està a BBDD consulta la petició i la guarda a BBDD
					AnotacioRegistreEntrada anotacioRegistreEntrada = distribucioHelper.consulta(idWs);

					// Guarda l'anotació
					Anotacio anotacioCreada = distribucioHelper.guardarAnotacio(id, anotacioRegistreEntrada);
					// Comprova si l'anotació s'ha associat amb un tipus d'expedient amb processament automàtic
					ExpedientTipus expedientTipus = anotacioCreada.getExpedientTipus();
					if (expedientTipus != null 
							&& expedientTipus.isDistribucioProcesAuto()) {
						if (expedientTipus.isDistribucioSistra()) {
							
						}
						// Crear l'expedient
						Expedient expedient = expedientHelper.iniciar(
								expedientTipus.getEntorn().getId(), //entornId
								null, //usuari, 
								expedientTipus.getId(), //expedientTipusId, 
								null, //definicioProcesId,
								null, //any, 
								null, //numero, 
								null, //titol, 
								null, //registreNumero, 
								null, //registreData, 
								null, //unitatAdministrativa, 
								null, //idioma, 
								false, //autenticat, 
								null, //tramitadorNif, 
								null, //tramitadorNom, 
								null, //interessatNif, 
								null, //interessatNom, 
								null, //representantNif, 
								null, //representantNom, 
								false, //avisosHabilitats, 
								null, //avisosEmail, 
								null, //avisosMobil, 
								false, //notificacioTelematicaHabilitada, 
								null, //variables, 
								null, //transitionName, 
								IniciadorTipusDto.INTERN, //IniciadorTipus 
								null, //iniciadorCodi, 
								null, //responsableCodi, 
								null, //documents, 
								null); //adjunts);
						// Incorporporar l'anotació a l'expedient
						anotacioService.incorporarExpedient(
								anotacioCreada.getId(), 
								expedientTipus.getId(), 
								expedient.getId(), 
								true);
						// Canvi d'estat a processada
						// Notifica a Distribucio que s'ha rebut correctament
						distribucioHelper.canviEstat(
								idWs, 
								es.caib.distribucio.ws.backofficeintegracio.Estat.PROCESSADA,
								"Petició processada a Helium.");
					} else {
						// Notifica a Distribucio que s'ha rebut correctament
						distribucioHelper.canviEstat(
								idWs, 
								es.caib.distribucio.ws.backofficeintegracio.Estat.REBUDA,
								"Petició rebuda a Helium.");
						
					}
					logger.info("Rebuda correctament la petició d'anotació de registre amb id de Distribucio =" + id);
					
					// commit transaction
					 */
					
				} else {
					// Si la petició ja existeix torna a comunicar el seu estat
					es.caib.distribucio.ws.backofficeintegracio.Estat estat = es.caib.distribucio.ws.backofficeintegracio.Estat.PENDENT;
					String msg = null;
					switch(anotacio.getEstat()) {
					case PENDENT:
						estat = es.caib.distribucio.ws.backofficeintegracio.Estat.REBUDA;
						msg = "La petició ja s'ha rebut anteriorment i està pendent de processar o rebutjar";
						break;
					case PROCESSADA:
						estat = es.caib.distribucio.ws.backofficeintegracio.Estat.PROCESSADA;
						msg = "La petició ja s'ha processat anteriorment.";
						break;
					case REBUTJADA:
						estat = es.caib.distribucio.ws.backofficeintegracio.Estat.REBUTJADA;
						msg = "La petició ja s'ha rebutjat anteriorment " + 
								(anotacio.getDataProcessament() != null? "amb data " + sdf.format(anotacio.getDataProcessament()) : "") + 
								"i motiu: " + anotacio.getRebuigMotiu();
						break;
					}
					// Comunica l'estat actual
					distribucioHelper.canviEstat(
							idWs, 
							estat,
							msg);
				}
			} catch(Exception e) {
				logger.error("Error rebent la petició d'anotació de registre amb id=" + id + " : " + e.getMessage() + ". Es comunica l'error a Distribucio", e);
				try {
					distribucioHelper.canviEstat(
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
