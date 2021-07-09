/**
 * 
 */
package es.caib.helium.logic.util.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.ByteArrayOutputStream;
import java.util.Set;

/**
 * SOAP Handler per a imprimir l'XML de peticions i repostes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SOAPLoggingHandler implements SOAPHandler<SOAPMessageContext> {

	public Set<QName> getHeaders() {
		return null;
	}

	public boolean handleMessage(SOAPMessageContext smc) {
		logToSystemOut(smc);
		return true;
	}

	public boolean handleFault(SOAPMessageContext smc) {
		logToSystemOut(smc);
		return true;
	}

	public void close(MessageContext messageContext) {
	}

	private void logToSystemOut(SOAPMessageContext smc) {
		if (LOGGER.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder();
			Boolean outboundProperty = (Boolean)smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (outboundProperty.booleanValue())
				sb.append("Missarge sortint: ");
			else
				sb.append("Missarge entrant: ");
			SOAPMessage message = smc.getMessage();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				message.writeTo(baos);
				sb.append(baos.toString());
			} catch (Exception ex) {
				sb.append("Error al imprimir el missatge XML: " + ex.getMessage());
			}
			LOGGER.debug(sb.toString());
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(SOAPLoggingHandler.class);

}
