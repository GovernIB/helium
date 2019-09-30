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
import net.conselldemallorca.helium.core.model.service.PluginService;
import net.conselldemallorca.helium.core.model.service.ServiceProxy;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioParametreDto;
import net.conselldemallorca.helium.ws.callback.MCGDwsImpl;

/**
 * Implementació dels mètodes per al servei de callback del portafirmes.
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
		switch (estat) {
		case 0:
		case 50:
			estat = MCGDwsImpl.DOCUMENT_PENDENT;
			break;
		case 60:
			estat = MCGDwsImpl.DOCUMENT_SIGNAT;
			break;
		case 70:
			estat = MCGDwsImpl.DOCUMENT_REBUTJAT;
			break;
		case 80:
			estat = MCGDwsImpl.DOCUMENT_BLOQUEJAT;
			break;
		default:
			String errorDescripcio = "No es reconeix el codi d'estat (" + estat + ")";
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_PFIRMA_CB,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					(IntegracioParametreDto[]) parametres.toArray());
			throw new CallBackException(errorDescripcio, new CallBackFault());
		}

		Double resposta = -1D;
		boolean processamentOk = false;
		String accio = null;
		try {
			PluginService pluginService = ServiceProxy.getInstance().getPluginService();
			switch (estat) {
				case MCGDwsImpl.DOCUMENT_BLOQUEJAT:
					resposta = 1D;
					accio = "Bloquejat";
					processamentOk = true;
					break;
				case MCGDwsImpl.DOCUMENT_PENDENT:
					resposta = 1D;
					accio = "Pendent";
					processamentOk = true;
					break;
				case MCGDwsImpl.DOCUMENT_SIGNAT:
					accio = "Signat";
					processamentOk = pluginService.processarDocumentCallbackPortasignatures(
							documentId.intValue(),
							false,
							null);
					resposta = (processamentOk) ? 1D : -1D;
					break;
				case MCGDwsImpl.DOCUMENT_REBUTJAT:
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
			monitorIntegracioHelper.addAccioOk(
					MonitorIntegracioHelper.INTCODI_PFIRMA_CB, 
					accioDescripcio, 
					IntegracioAccioTipusEnumDto.RECEPCIO, 
					System.currentTimeMillis() - t0, 
					Arrays.copyOf(parametres.toArray(), parametres.size(), IntegracioParametreDto[].class));
		} catch (Exception ex) {
			logger.error("Error procés petició callback portasignatures (id=" + documentId + ", estat=" + estat + ", resposta=" + resposta + "): " + ex.getMessage());
			String errorDescripcio = "El procés petició callback del portasignatures no ha finalitzat correctament";
			parametres.add(new IntegracioParametreDto("processamentOk", processamentOk));
			monitorIntegracioHelper.addAccioError(
					MonitorIntegracioHelper.INTCODI_PFIRMA_CB,
					accioDescripcio,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					Arrays.copyOf(parametres.toArray(), parametres.size(), IntegracioParametreDto[].class));
			throw new CallBackException(errorDescripcio, new CallBackFault());
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(PortaFIBCallBackWsImpl.class);

}