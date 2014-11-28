/**
 * MCGDwsImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.conselldemallorca.helium.ws.callback;

import java.io.PrintWriter;
import java.io.StringWriter;

import net.conselldemallorca.helium.core.model.service.PluginService;
import net.conselldemallorca.helium.core.model.service.ServiceProxy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.indra.www.portafirmasmcgdws.mcgdws.AttributesState;
import es.indra.www.portafirmasmcgdws.mcgdws.CallbackRequest;
import es.indra.www.portafirmasmcgdws.mcgdws.CallbackResponse;
import es.indra.www.portafirmasmcgdws.mcgdws.LogMessage;

/**
 * Implementació amb Axis del servei per processar automàticament els canvis
 * del portasignatures.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class MCGDwsImpl implements es.indra.www.portafirmasmcgdws.mcgdws.MCGDws {

	private static final int DOCUMENT_BLOQUEJAT = 0;
	private static final int DOCUMENT_PENDENT = 1;
	private static final int DOCUMENT_SIGNAT = 2;
	private static final int DOCUMENT_REBUTJAT = 3;

	public CallbackResponse callback(CallbackRequest callbackRequest) throws java.rmi.RemoteException {
		Integer document = callbackRequest.getApplication().getDocument().getId();
		AttributesState estat = callbackRequest.getApplication().getDocument().getAttributes().getState();
		logger.info("Inici procés petició callback portasignatures (id=" + document + ", estat=" + estat.getValue() + ")");
		CallbackResponse callbackResponse = new CallbackResponse();
		try {
			PluginService pluginService = ServiceProxy.getInstance().getPluginService();
			Double resposta = -1D;
			boolean processamentOk;
			try {
				switch (estat.getValue()) {
					case DOCUMENT_BLOQUEJAT:
						resposta = 1D;
						logger.info("Fi procés petició callback portasignatures (id=" + document + ", estat=" + estat.getValue() + "-Bloquejat, resposta=" + resposta + ")");
						break;
					case DOCUMENT_PENDENT:
						resposta = 1D;
						logger.info("Fi procés petició callback portasignatures (id=" + document + ", estat=" + estat.getValue() + "-Pendent, resposta=" + resposta + ")");
						break;
					case DOCUMENT_SIGNAT:
						processamentOk = pluginService.processarDocumentCallbackPortasignatures(
								document,
								false,
								null);
						resposta = (processamentOk) ? 1D : -1D;
						logger.info("Fi procés petició callback portasignatures (id=" + document + ", estat=" + estat.getValue() + "-Signat, resposta=" + resposta + ")");
						break;
					case DOCUMENT_REBUTJAT:
						processamentOk = pluginService.processarDocumentCallbackPortasignatures(
								document,
								true,
								callbackRequest.getApplication().getDocument().getSigner().getRejection().getDescription());
						resposta = (processamentOk) ? 1D : -1D;
						logger.info("Fi procés petició callback portasignatures (id=" + document + ", estat=" + estat.getValue() + "-Rebutjat, resposta=" + resposta + ")");
						break;
					default:
						break;
				}
			} catch (Exception ex) {
				logger.error("Error procés petició callback portasignatures (id=" + document + ", estat=" + estat.getValue() + ", resposta=" + resposta + "): " + ex.getMessage());
			}
			callbackResponse.setVersion("1.0");
			callbackResponse.setLogMessages(new LogMessage[0]);
			callbackResponse.set_return(resposta.doubleValue());
		} catch (Exception e) {
			logger.error("Error obtenint l'estat del document.", e);
			callbackResponse.setVersion("1.0");
			LogMessage logMessage = new LogMessage();
			logMessage.setCode("-1");
			logMessage.setSeverity("High");
			logMessage.setTitle(e.getMessage());
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			logMessage.setDescription(sw.toString());
			callbackResponse.setLogMessages(new LogMessage[] { logMessage });
			callbackResponse.set_return((double) -1);
		}
		return callbackResponse;
	}

	private static final Log logger = LogFactory.getLog(MCGDwsImpl.class);

}
