/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service.ws;

import java.util.ArrayList;
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
import net.conselldemallorca.helium.core.helper.PortafirmesCallbackHelper;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioAccioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.IntegracioParametreDto;

/**
 * Implementació del servei de callback de portafirmes amb interfície PORTAFIB.
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
	private PortafirmesCallbackHelper portafirmesCallbackHelper;

	@Override
	public int getVersionWs() {
		return 1;
	}

	@Override
	public void event(PortaFIBEvent event) throws CallBackException {
		long documentId = event.getSigningRequest().getID();
		int estat = event.getEventTypeID();
		logger.debug("Rebuda petició al callback de portafirmes (" +
				"documentId:" + documentId + ", " +
				"estat:" + estat + ")");
		long t0 = System.currentTimeMillis();
		String accioDescripcio = "Petició rebuda al callback de portafirmes MCGDws";
		List<IntegracioParametreDto> parametres = new ArrayList<IntegracioParametreDto>();
		parametres.add(new IntegracioParametreDto("documentId", documentId));
		parametres.add(new IntegracioParametreDto("estat", estat));
		boolean estatFinal = false;
		boolean rebutjat = false;
		switch (estat) {
		case 0:
		case 50: // PENDENT
			break;
		case 60: // FIRMAT
			estatFinal = true;
			rebutjat = false;
			break;
		case 70: // REBUTJAT
			estatFinal = true;
			rebutjat = true;
			break;
		case 80: // PAUSAT
			break;
		default:
			String errorDescripcio = "No es reconeix el codi d'estat (" + estat + ")";
			throw new CallBackException(errorDescripcio, new CallBackFault());
		}
		if (estatFinal) {
			boolean respostaOk = false;
			String respostaError = null;
			try {
				String motiuRebuig = null;
				if (rebutjat && event.getSigningRequest() != null ) {
					motiuRebuig = event.getSigningRequest().getRejectionReason();
				}
				respostaOk = portafirmesCallbackHelper.processarDocumentCallbackPortasignatures(
						documentId,
						rebutjat,
						motiuRebuig);
				if (respostaOk) {
					monitorIntegracioHelper.addAccioOk(
							MonitorIntegracioHelper.INTCODI_PFIRMA_CB, 
							accioDescripcio, 
							IntegracioAccioTipusEnumDto.RECEPCIO, 
							System.currentTimeMillis() - t0, 
							(IntegracioParametreDto[])parametres.toArray());
				} else {
					String errorDescripcio = "El processament del callback de portafirmes MCGDws no ha finalitzat correctament";
					respostaError = errorDescripcio;
					monitorIntegracioHelper.addAccioError(
							MonitorIntegracioHelper.INTCODI_PFIRMA_CB,
							accioDescripcio,
							IntegracioAccioTipusEnumDto.RECEPCIO,
							System.currentTimeMillis() - t0,
							errorDescripcio,
							(IntegracioParametreDto[])parametres.toArray());
				}
			} catch (Exception ex) {
				String errorDescripcio = "Excepció al processar petició rebuda al callback de portafirmes MCGDws";
				respostaError = errorDescripcio;
				monitorIntegracioHelper.addAccioError(
						MonitorIntegracioHelper.INTCODI_PFIRMA_CB,
						accioDescripcio,
						IntegracioAccioTipusEnumDto.RECEPCIO,
						System.currentTimeMillis() - t0,
						errorDescripcio,
						ex,
						(IntegracioParametreDto[]) parametres.toArray());
				respostaOk = false;
			}
			if (!respostaOk) {
				throw new CallBackException(respostaError, new CallBackFault());
			}
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(PortaFIBCallBackWsImpl.class);

}
