/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.signatura.afirma;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.SOAPPart;
import org.apache.axis.handlers.BasicHandler;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.message.WSSecHeader;
import org.apache.ws.security.message.WSSecUsernameToken;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Modifica peticions SOAP per incorporar les capçaleres
 * WS-Security 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AfirmaSecurityHandler extends BasicHandler {
	private static final long serialVersionUID = 1L;

	private String username;
	private String password;
	
	public AfirmaSecurityHandler(String usuari, String password) {
		this.username = usuari;
		this.password = password;
	}
	
	public void invoke(MessageContext msgContext) throws AxisFault {
		try {
			msgContext.setProperty(AxisEngine.PROP_DISABLE_PRETTY_XML, true);

			SOAPMessage msg = msgContext.getCurrentMessage();
			Document doc = ((org.apache.axis.message.SOAPEnvelope)msg.getSOAPPart().getEnvelope()).getAsDocument();

			SOAPMessage secMsg = this.createUserNameToken(doc);
			((SOAPPart)msgContext.getRequestMessage().getSOAPPart()).setCurrentMessage(secMsg.getSOAPPart().getEnvelope(), SOAPPart.FORM_SOAPENVELOPE);
		} catch (Exception ex) {
			throw new AxisFault("(Invoke) Error en la cridada a aFirma " + ex.getMessage(), ex);
		}
	}

	private SOAPMessage createUserNameToken(Document soapEnvelopeRequest) throws IOException, SOAPException, TransformerException, WSSecurityException {
		WSSecHeader wsSecHeader = new WSSecHeader(null, false);
		wsSecHeader.insertSecurityHeader(soapEnvelopeRequest);
		
		WSSecUsernameToken wsSecUsernameToken = new WSSecUsernameToken();
		wsSecUsernameToken.setUserInfo(this.username, this.password);
		wsSecUsernameToken.prepare(soapEnvelopeRequest);
		wsSecUsernameToken.addCreated();
		wsSecUsernameToken.addNonce();

		Document secSOAPReqDoc = wsSecUsernameToken.build(soapEnvelopeRequest, wsSecHeader);
		Element element = secSOAPReqDoc.getDocumentElement();

		DOMSource source = new DOMSource(element);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		StreamResult streamResult = new StreamResult(baos);
		TransformerFactory.newInstance().newTransformer().transform(source, streamResult);

		String secSOAPReq = new String(baos.toByteArray());
		SOAPMessage res = new org.apache.axis.soap.MessageFactoryImpl().createMessage(null, new ByteArrayInputStream(secSOAPReq.getBytes()));

		return res;
	}
}
