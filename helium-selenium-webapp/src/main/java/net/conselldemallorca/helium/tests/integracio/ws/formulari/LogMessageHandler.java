/**
 * 
 */
package net.conselldemallorca.helium.tests.integracio.ws.formulari;

import java.io.StringWriter;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/**
 * MessageHandler que imprimeix els missatges intercanviats per la
 * consola.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class LogMessageHandler implements SOAPHandler<SOAPMessageContext> {

	public boolean handleMessage(SOAPMessageContext messageContext) {
		log(messageContext);
		return true;
	}
	public Set<QName> getHeaders() {
		return Collections.emptySet();
	}
	public boolean handleFault(SOAPMessageContext messageContext) {
		log(messageContext);
		return true;
	}
	public void close(MessageContext context) {
	}
	private void log(SOAPMessageContext messageContext) {
		try {
			Boolean outboundProperty = (Boolean)messageContext.get(
					MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (outboundProperty)
				System.out.print("Missatge SOAP de la petició: ");
			else
				System.out.print("Missatge SOAP de la resposta: ");
			System.out.println(messageToString(messageContext));
		} catch (Exception ex) {
			Logger.getLogger(LogMessageHandler.class.getName()).log(
					Level.SEVERE,
					null,
					ex);
		}
	}

	private String messageToString(
			SOAPMessageContext messageContext) throws TransformerFactoryConfigurationError, TransformerException, SOAPException {
		SOAPMessage message = messageContext.getMessage();
		SOAPPart part = message.getSOAPPart();
		if (part.getEnvelope() == null)
			return null;
		StringWriter writer = new StringWriter();
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(new DOMSource(part.getEnvelope()), new StreamResult(writer));
		return writer.toString();
	}

}
