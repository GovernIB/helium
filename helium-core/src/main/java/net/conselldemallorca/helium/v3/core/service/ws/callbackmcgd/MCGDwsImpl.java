/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service.ws.callbackmcgd;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.core.helper.MonitorIntegracioHelper;
import net.conselldemallorca.helium.core.helper.PortafirmesCallbackHelper;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioParametreDto;

/**
 * Implementació del servei de callback de portafirmes amb interfície MCGD.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
@WebService(
		name = "MCGDws",
		serviceName = "MCGDwsService",
		portName = "MCGDwsServicePort",
		targetNamespace = "http://www.indra.es/portafirmasmcgdws/mcgdws",
		endpointInterface = "es.caib.ripea.core.service.ws.callback.MCGDws")
public class MCGDwsImpl implements MCGDws {

	@Autowired
	private MonitorIntegracioHelper monitorIntegracioHelper;
	@Autowired
	private PortafirmesCallbackHelper portafirmesCallbackHelper;

	@Override
	public CallbackResponse callback(
			CallbackRequest callbackRequest) {
		int documentId = callbackRequest.getApplication().getDocument().getId();
		Integer estat = callbackRequest.getApplication().getDocument().getAttributes().getState();
		logger.debug("Rebuda petició al callback de portafirmes (" +
				"documentId:" + documentId + ", " +
				"estat:" + estat + ")");
		long t0 = System.currentTimeMillis();
		String accioDescripcio = "Petició rebuda al callback de portafirmes MCGDws";
		List<IntegracioParametreDto> parametres = new ArrayList<IntegracioParametreDto>();
		parametres.add(new IntegracioParametreDto("documentId", new Long(documentId).toString()));
		parametres.add(new IntegracioParametreDto("estat", estat.toString()));
		CallbackResponse callbackResponse = new CallbackResponse();
		boolean estatFinal = false;
		boolean rebutjat = false;
		switch (estat) {
		case 0: // PAUSAT
			break;
		case 1: // PENDENT
			break;
		case 2: // FIRMAT
			estatFinal = true;
			rebutjat = false;
			break;
		case 3: // REBUTJAT
			estatFinal = true;
			rebutjat = true;
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
			callbackResponse.setReturn(-1);
		}
		if (estatFinal) {
			try {
				String motiuRebuig = null;
				if (rebutjat) {
					motiuRebuig = callbackRequest.getApplication().getDocument().getSigner().getRejection().getDescription();
				}
				boolean processamentOk = portafirmesCallbackHelper.processarDocumentCallbackPortasignatures(
						documentId,
						rebutjat,
						motiuRebuig);
				if (processamentOk) {
					monitorIntegracioHelper.addAccioOk(
							MonitorIntegracioHelper.INTCODI_PFIRMA_CB, 
							accioDescripcio, 
							IntegracioAccioTipusEnumDto.RECEPCIO, 
							System.currentTimeMillis() - t0, 
							(IntegracioParametreDto[])parametres.toArray());
					callbackResponse.setReturn(1);
				} else {
					String errorDescripcio = "El processament del callback de portafirmes MCGDws no ha finalitzat correctament";
					monitorIntegracioHelper.addAccioError(
							MonitorIntegracioHelper.INTCODI_PFIRMA_CB,
							accioDescripcio,
							IntegracioAccioTipusEnumDto.RECEPCIO,
							System.currentTimeMillis() - t0,
							errorDescripcio,
							(IntegracioParametreDto[])parametres.toArray());
					callbackResponse.setReturn(-1);
				}
			} catch (Exception ex) {
				String errorDescripcio = "Excepció al processar petició rebuda al callback de portafirmes MCGDws";
				monitorIntegracioHelper.addAccioError(
						MonitorIntegracioHelper.INTCODI_PFIRMA_CB,
						accioDescripcio,
						IntegracioAccioTipusEnumDto.RECEPCIO,
						System.currentTimeMillis() - t0,
						errorDescripcio,
						ex,
						(IntegracioParametreDto[]) parametres.toArray());
				callbackResponse.setReturn(-1);
			}
		}
		return callbackResponse;
	}

	private static final Logger logger = LoggerFactory.getLogger(MCGDwsImpl.class);

}
