package es.caib.helium.integracio.service.portafirmes;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

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
		
		SOAPMessage msg = messageContext.getMessage();
		try {
			Boolean outboundProperty = (Boolean)messageContext.get(
					MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (outboundProperty) {
				System.out.print("Missatge SOAP petició: ");
			} else {
				System.out.print("Missatge SOAP resposta: ");
			}
			msg.writeTo(System.out);
			System.out.println();
		} catch (SOAPException ex) {
			Logger.getLogger(LogMessageHandler.class.getName()).log(
					Level.SEVERE,
					null,
					ex);
		} catch (IOException ex) {
			Logger.getLogger(LogMessageHandler.class.getName()).log(
					Level.SEVERE,
					null,
					ex);
		}
	}
}
