/**
 * MCGDwsImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.conselldemallorca.helium.ws.callback;

import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.indra.www.portafirmasmcgdws.mcgdws.AttributesState;
import es.indra.www.portafirmasmcgdws.mcgdws.CallbackRequest;
import es.indra.www.portafirmasmcgdws.mcgdws.CallbackResponse;
import es.indra.www.portafirmasmcgdws.mcgdws.LogMessage;
import net.conselldemallorca.helium.core.helper.PluginHelper;
import net.conselldemallorca.helium.v3.core.api.service.PortasignaturesService;

/**
 * Implementació amb Axis del servei per processar automàticament els canvis
 * del portasignatures.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class MCGDwsImpl implements es.indra.www.portafirmasmcgdws.mcgdws.MCGDws {

	@Autowired
	private PortasignaturesService portasignaturesService;
	
	public static final int DOCUMENT_BLOQUEJAT = 0;
	public static final int DOCUMENT_PENDENT = 1;
	public static final int DOCUMENT_SIGNAT = 2;
	public static final int DOCUMENT_REBUTJAT = 3;

	public CallbackResponse callback(CallbackRequest callbackRequest) throws java.rmi.RemoteException {
		Integer document = callbackRequest.getApplication().getDocument().getId();
		AttributesState estat = callbackRequest.getApplication().getDocument().getAttributes().getState();
		logger.info("Inici procés petició callback portasignatures (id=" + document + ", estat=" + estat.getValue() + ")");
		CallbackResponse callbackResponse = new CallbackResponse();
		callbackResponse.setVersion("1.0");
		try {
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
						processamentOk = portasignaturesService.processarDocumentCallback(
								document,
								false,
								null);
						resposta = (processamentOk) ? 1D : -1D;
						logger.info("Fi procés petició callback portasignatures (id=" + document + ", estat=" + estat.getValue() + "-Signat, resposta=" + resposta + ")");
						break;
					case DOCUMENT_REBUTJAT:
						processamentOk = portasignaturesService.processarDocumentCallback(
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
			callbackResponse.setLogMessages(new LogMessage[0]);
			callbackResponse.set_return(resposta.doubleValue());
		} catch (Exception e) {
			logger.error("Error obtenint l'estat del document.", e);
			LogMessage logMessage = new LogMessage();
			logMessage.setCode("-1");
			logMessage.setSeverity("High");
			logMessage.setTitle(e.getMessage());
			StringWriter sw = new StringWriter();
			logMessage.setDescription(sw.toString());
			callbackResponse.setLogMessages(new LogMessage[] { logMessage });
			callbackResponse.set_return((double) -1);
		}
		return callbackResponse;
	}

	private static final Logger logger = LoggerFactory.getLogger(MCGDwsImpl.class);

}
