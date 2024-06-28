/**
 * 
 */
package net.conselldemallorca.helium.ws.callbackportafib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.portafib.ws.callback.api.v1.CallBackException;
import es.caib.portafib.ws.callback.api.v1.CallBackFault;
import es.caib.portafib.ws.callback.api.v1.PortaFIBCallBackWs;
import es.caib.portafib.ws.callback.api.v1.PortaFIBEvent;
import net.conselldemallorca.helium.core.helper.MonitorIntegracioHelper;
import net.conselldemallorca.helium.core.helper.PortasignaturesHelper;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures;
import net.conselldemallorca.helium.core.model.service.PluginService;
import net.conselldemallorca.helium.core.model.service.ServiceProxy;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioParametreDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesEstatEnum;

/**
 * Implementació dels mètodes per al servei de callback del portafirmes per WS SOAP 1.0.
 * També és possible rebre els callbacks per API REST al PortaFIBCallback.
 * 
 * @see net.conselldemallorca.helium.webapp.v3.rest.PortaFIBCallback
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
@WebService(
		name = "PortaFIBCallBackWs",
		serviceName = "PortaFIBCallBackWsService",
		portName = "PortaFIBCallBackWs",
		targetNamespace = "http://v1.server.callback.ws.portafib.caib.es/",
		endpointInterface = "es.caib.portafib.ws.callback.api.v1.PortaFIBCallBackWs")
public class PortaFIBCallBackWsImpl implements PortaFIBCallBackWs {

	@Autowired
	private MonitorIntegracioHelper monitorIntegracioHelper;
	@Autowired
	private PortasignaturesHelper portasignaturesHelper;


	@Override
	public int getVersionWs() {
		return 1;
	}

	@Override
	public void event(PortaFIBEvent event) throws CallBackException {
		Long documentId = event.getSigningRequest().getID();
		Integer estat = event.getEventTypeID();
		logger.debug("Rebuda petició al callback de portafirmes (" +
							"documentId:" + documentId + ", " +
							"estat:" + estat + ")");
		long t0 = System.currentTimeMillis();
		String accioDescripcio = "Petició rebuda al callback WS 1.0";
		List<IntegracioParametreDto> parametres = new ArrayList<IntegracioParametreDto>();
		parametres.add(new IntegracioParametreDto("documentId", new Long(documentId).toString()));
		parametres.add(new IntegracioParametreDto("estat", new Integer(estat).toString()));
		// Transforma el codi d'estat
		PortafirmesEstatEnum tipusEstat;
		switch (estat) {
		case 0:
		case 50:
			tipusEstat = PortafirmesEstatEnum.PENDENT;
			break;
		case 60:
			tipusEstat = PortafirmesEstatEnum.SIGNAT;
			break;
		case 70:
			tipusEstat = PortafirmesEstatEnum.REBUTJAT;
			break;
		case 80:
			tipusEstat = PortafirmesEstatEnum.BLOQUEJAT;
			break;
		default:
			String errorDescripcio = "No es reconeix el codi d'estat (" + estat + ")";
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_PFIRMA,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					parametres.toArray(new IntegracioParametreDto[parametres.size()]));
			throw new CallBackException(errorDescripcio, new CallBackFault());
		}

		// Comprova si existeix la petició
		Portasignatures portasignatures = portasignaturesHelper.getByDocumentId(documentId.intValue());
		if (portasignatures != null) {
			Double resposta = -1D;
			boolean processamentOk = false;
			String accio = null;
		try {
				PluginService pluginService = ServiceProxy.getInstance().getPluginService();
				switch (tipusEstat) {
					case BLOQUEJAT:
						resposta = 1D;
						accio = "Bloquejat";
						processamentOk = true;
						break;
					case PENDENT:
						resposta = 1D;
						accio = "Pendent";
						processamentOk = true;
						break;
					case SIGNAT:
						accio = "Signat";
						processamentOk = pluginService.processarDocumentCallbackPortasignatures(
								documentId.intValue(),
								false,
								null);
						resposta = (processamentOk) ? 1D : -1D;
						break;
					case REBUTJAT:
						accio = "Rebutjat";
						String motiu = null;
						if (event.getSigningRequest() != null )
							motiu = event.getSigningRequest().getRejectionReason();
						processamentOk = pluginService.processarDocumentCallbackPortasignatures(
								documentId.intValue(),
								true,
								motiu);
						resposta = (processamentOk) ? 1D : -1D;
						break;
					default:
						break;
				}
				logger.info("Fi procés petició callback portasignatures (id=" + documentId + ", estat=" + estat + "-" + accio + ", resposta=" + resposta + ")");
				if (!processamentOk)
					throw new Exception("El document no s'ha processat correctament (id=" + documentId + ", estat=" + estat + "-Signat, resposta=" + resposta + ")");
			} catch (Exception ex) {
				logger.error("Error procés petició callback portasignatures (id=" + documentId + ", estat=" + estat + ", resposta=" + resposta + "): " + ex.getMessage());
				String errorDescripcio = "El procés petició callback del portasignatures no ha finalitzat correctament";
				parametres.add(new IntegracioParametreDto("processamentOk", processamentOk));
				monitorIntegracioHelper.addAccioError(
						MonitorIntegracioHelper.INTCODI_PFIRMA,
					accioDescripcio,
						IntegracioAccioTipusEnumDto.RECEPCIO,
						System.currentTimeMillis() - t0,
						errorDescripcio,
						Arrays.copyOf(parametres.toArray(), parametres.size(), IntegracioParametreDto[].class));
			throw new CallBackException(errorDescripcio, new CallBackFault());
		}
		} else {
			// Avís als logs en comtpes de retorna error #1413
			String warnMsg = "Petició amb id " + documentId + " no trobada a Helium.";
			parametres.add(new IntegracioParametreDto("advertencia", warnMsg));
			logger.warn(warnMsg);
		}
		monitorIntegracioHelper.addAccioOk(
				MonitorIntegracioHelper.INTCODI_PFIRMA, 
				accioDescripcio, 
				IntegracioAccioTipusEnumDto.RECEPCIO, 
				System.currentTimeMillis() - t0, 
				Arrays.copyOf(parametres.toArray(), parametres.size(), IntegracioParametreDto[].class));

	}

	private static final Logger logger = LoggerFactory.getLogger(PortaFIBCallBackWsImpl.class);

}
