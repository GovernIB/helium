/**
 * MCGDwsImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.conselldemallorca.helium.integracio.plugins.portasignatures.service;

import net.conselldemallorca.helium.model.service.PluginService;
import net.conselldemallorca.helium.model.service.ServiceProxy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.indra.www.portafirmasmcgdws.mcgdws.AttributesState;
import es.indra.www.portafirmasmcgdws.mcgdws.CallbackRequest;
import es.indra.www.portafirmasmcgdws.mcgdws.CallbackResponse;

/**
 * Implementació amb Axis del servei per processar automàticament els canvis
 * del portasignatures.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class MCGDwsImpl implements es.indra.www.portafirmasmcgdws.mcgdws.MCGDws{

	private static final int DOCUMENT_BLOQUEJAT = 0;
	private static final int DOCUMENT_PENDENT = 1;
	private static final int DOCUMENT_SIGNAT = 2;
	private static final int DOCUMENT_REBUTJAT = 3;

	public CallbackResponse callback(CallbackRequest callbackRequest) throws java.rmi.RemoteException {
		Integer document = callbackRequest.getApplication().getDocument().getId();
		logger.info("Rebuda petició callback portasignatures del document " + document);
		CallbackResponse callbackResponse = new CallbackResponse();
		try {
			AttributesState estat = callbackRequest.getApplication().getDocument().getAttributes().getState();
			PluginService pluginService = ServiceProxy.getInstance().getPluginService();
			Double resposta = -1D;
			switch (estat.getValue()) {
				case DOCUMENT_BLOQUEJAT:
					resposta = 1D;
					logger.info("Document " + document + " bloquejat (" + resposta + ").");
					break;
				case DOCUMENT_PENDENT:
					resposta = 1D;
					logger.info("Document " + document + " pendent (" + resposta + ").");
					break;
				case DOCUMENT_SIGNAT:
					resposta = pluginService.processarDocumentSignatPortasignatures(
							document);
					logger.info("Document " + document + " signat (" + resposta + ").");
					break;
				case DOCUMENT_REBUTJAT:
					resposta = pluginService.processarDocumentRebutjatPortasignatures(
							document,
							callbackRequest.getApplication().getDocument().getSigner().getRejection().getDescription());
					logger.info("Document " + document + " rebutjat (" + resposta + ").");
					break;
				default:
					break;
			}
			//callbackResponse.setLogMessages(logMessages)setLogMessages(new ArrayOfLogMessage());
	        callbackResponse.setVersion("1.0");
	        callbackResponse.set_return(resposta.doubleValue());
		} catch (Exception e) {
			logger.error("Error obtenint l'estat del document.", e);
			//callbackResponse.setLogMessages(new ArrayOfLogMessage());
			callbackResponse.setVersion("1.0");
			callbackResponse.set_return((double)-1);
		}
		return callbackResponse;
	}

	private static final Log logger = LogFactory.getLog(MCGDwsImpl.class);

}
