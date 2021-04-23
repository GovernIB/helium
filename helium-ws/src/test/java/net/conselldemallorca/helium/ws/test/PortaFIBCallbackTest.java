/**
 * 
 */
package net.conselldemallorca.helium.ws.test;

import es.caib.portafib.ws.callback.api.v1.PortaFIBCallBackWs;
import es.caib.portafib.ws.callback.api.v1.PortaFIBEvent;
import es.caib.portafib.ws.callback.api.v1.SigningRequest;
import net.conselldemallorca.helium.core.util.ws.WsClientUtils;

/**
 * Test per invocar el callback del portafirmes del WS de Helium que implementa la versió més nova
 * del WS PortaFIB Callback 1.0.
 *
 */
public class PortaFIBCallbackTest {

	private static String ENDPOINT_ADDRESS = "http://10.35.3.231:8080/helium/ws/v1/PortaFIBCallBack";
	private static String USERNAME = "admin";
	private static String PASSWORD = "admin15";


	public static void main(String[] args) {
		try {
			// Estats:
			//   0  - DOCUMENT_PENDENT;
			//   50 - DOCUMENT_PENDENT;
			//   60 - DOCUMENT_FIRMAT;
			//   70 - DOCUMENT_REBUTJAT;
			//   80 - DOCUMENT_PAUSAT;
			long psignaDocumentId = 123;
			int estat = 60;
			new PortaFIBCallbackTest().testCallback(
					psignaDocumentId, 
					estat);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void testCallback(long documentId, int estat) throws Exception {
		PortaFIBEvent event = new PortaFIBEvent();
		SigningRequest signingRequest = new SigningRequest();
		signingRequest.setID(documentId);
		signingRequest.setState(3);
		signingRequest.setRejectionReason("Raó rebuig");
		event.setSigningRequest(signingRequest);
		event.setEventTypeID(estat);
		
		// Crida al WS callback del portasignatures		
		getClientFormulariExtern().event(event);
		
	}
	
	private PortaFIBCallBackWs getClientFormulariExtern() {
		return (PortaFIBCallBackWs) WsClientUtils.getWsClientProxy(
				PortaFIBCallBackWs.class,
				ENDPOINT_ADDRESS,
				USERNAME,
				PASSWORD,
				USERNAME != null && PASSWORD != null ? "BASIC" : "NONE",
				false,
				true,
				true,
				null);
	}
}
