/**
 * 
 */
package net.conselldemallorca.helium.integracio.bantel.service;

import java.util.Map;

import javax.jws.WebService;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import net.conselldemallorca.helium.integracio.bantel.client.wsdl.BackofficeFacade;
import net.conselldemallorca.helium.integracio.bantel.plugin.DadesDocument;
import net.conselldemallorca.helium.integracio.bantel.plugin.DadesIniciExpedient;
import net.conselldemallorca.helium.integracio.bantel.plugin.IniciExpedientPlugin;
import net.conselldemallorca.helium.integracio.bantel.service.wsdl.BantelFacade;
import net.conselldemallorca.helium.integracio.bantel.service.wsdl.BantelFacadeException_Exception;
import net.conselldemallorca.helium.integracio.bantel.service.wsdl.ReferenciaEntrada;
import net.conselldemallorca.helium.integracio.bantel.service.wsdl.ReferenciasEntrada;
import net.conselldemallorca.helium.model.dto.ExpedientDto;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.hibernate.Expedient;
import net.conselldemallorca.helium.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.model.hibernate.Expedient.IniciadorTipus;
import net.conselldemallorca.helium.model.service.DissenyService;
import net.conselldemallorca.helium.model.service.EntornService;
import net.conselldemallorca.helium.model.service.ExpedientService;
import net.conselldemallorca.helium.util.EntornActual;
import net.conselldemallorca.helium.util.GlobalProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * Implementació del servei per processar automàticament els
 * tràmits que arriben al mòdul BANTEL.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@WebService(endpointInterface = "net.conselldemallorca.helium.integracio.bantel.service.wsdl.BantelFacade")
public class BantelFacadeImpl implements BantelFacade {

	private BackofficeFacade sistraBantelClient;
	private ExpedientService expedientService;
	private EntornService entornService;
	private DissenyService dissenyService;

	private IniciExpedientPlugin iniciExpedientPlugin;
	private IniciExpedientPlugin defaultIniciExpedientPlugin;



	public void avisoEntradas(ReferenciasEntrada numeroEntradas) throws BantelFacadeException_Exception {
		Assert.notNull(numeroEntradas, "És obligatori especificar les entrades");
		logger.info("Notificació d'arribada de tràmits des de BANTEL");
		for (ReferenciaEntrada ref: numeroEntradas.getReferenciaEntrada()) {
			net.conselldemallorca.helium.integracio.bantel.client.wsdl.ReferenciaEntrada refClient = new net.conselldemallorca.helium.integracio.bantel.client.wsdl.ReferenciaEntrada();
			refClient.setClaveAcceso(new JAXBElement<String>(new QName("claveAcceso"), String.class, ref.getClaveAcceso()));
			refClient.setNumeroEntrada(ref.getNumeroEntrada());
			logger.info("Processant tràmit " + ref.getNumeroEntrada());
			try {
				DadesIniciExpedient dadesIniciExpedient = getIniciExpedientPlugin().obtenirDadesInici(
						sistraBantelClient.obtenerEntrada(refClient));
				Entorn entorn = entornService.findAmbCodi(dadesIniciExpedient.getEntornCodi());
				ExpedientTipus expedientTipus = dissenyService.findExpedientTipusAmbEntornICodi(
						entorn.getId(),
						dadesIniciExpedient.getTipusCodi());
				EntornActual.setEntornId(entorn.getId());
				ExpedientDto nouExpedient = expedientService.iniciar(
						entorn.getId(),
						expedientTipus.getId(),
						null,
						dadesIniciExpedient.getNumero(),
						dadesIniciExpedient.getTitol(),
						dadesIniciExpedient.getDadesInicials(),
						dadesIniciExpedient.getTransitionName(),
						IniciadorTipus.SISTRA,
						Expedient.crearIniciadorCodiPerSistra(ref.getNumeroEntrada(), ref.getClaveAcceso()),
						null);
				
				//Afegim els documents
				Map<String,DadesDocument> documents = dadesIniciExpedient.getDocumentsInicials();
				if(documents != null && !documents.isEmpty()){
					for (Map.Entry<String, DadesDocument> doc: documents.entrySet()){
						expedientService.guardarDocument(
								nouExpedient.getProcessInstanceId(), 
								doc.getValue().getIdDocument(), 
								doc.getValue().getData(), 
								doc.getValue().getArxiuNom(), 
								doc.getValue().getArxiuContingut());
					}
				}
				
				logger.info("Nou expedient creat per al tràmit " + ref.getNumeroEntrada());
				sistraBantelClient.establecerResultadoProceso(
						refClient,
						"S",
						null);
			} catch (Exception ex) {
				try {
					sistraBantelClient.establecerResultadoProceso(
							refClient,
							"X",
							ex.getMessage());
				} catch (Exception ex2) {
					logger.error(
							"No s'ha pogut notificar l'error en el processament del tràmit " + ref.getNumeroEntrada(),
							ex2);
				}
				logger.error(
						"No s'ha pogut obtenir la informació del tràmit " + ref.getNumeroEntrada(),
						ex);
				throw new BantelFacadeException_Exception(
						"No s'ha pogut obtenir la informació del tràmit " + ref.getNumeroEntrada(),
						ex);
			}
		}
	}



	public void setSistraBantelClient(BackofficeFacade sistraBantelClient) {
		this.sistraBantelClient = sistraBantelClient;
	}
	@Autowired
	public void setExpedientService(ExpedientService expedientService) {
		this.expedientService = expedientService;
	}
	@Autowired
	public void setEntornService(EntornService entornService) {
		this.entornService = entornService;
	}
	@Autowired
	public void setDissenyService(DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}

	public void setDefaultIniciExpedientPlugin(
			IniciExpedientPlugin defaultIniciExpedientPlugin) {
		this.defaultIniciExpedientPlugin = defaultIniciExpedientPlugin;
	}



	@SuppressWarnings("unchecked")
	private IniciExpedientPlugin getIniciExpedientPlugin() throws Exception {
		if (iniciExpedientPlugin == null) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.bantel.plugin.class");
			if (pluginClass != null && pluginClass.length() > 0) {
				Class clazz = Class.forName(pluginClass);
				iniciExpedientPlugin = (IniciExpedientPlugin)clazz.newInstance();
			} else {
				iniciExpedientPlugin = defaultIniciExpedientPlugin;
			}
		}
		return iniciExpedientPlugin;
	}

	private static final Log logger = LogFactory.getLog(BantelFacadeImpl.class);

}
