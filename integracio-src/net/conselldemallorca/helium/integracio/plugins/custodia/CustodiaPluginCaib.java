/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.custodia;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.conselldemallorca.helium.integracio.plugins.signatura.InfoCertificat;
import net.conselldemallorca.helium.integracio.plugins.signatura.InfoSignatura;
import net.conselldemallorca.helium.util.GlobalProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Implementació del plugin de custodia documental que guarda
 * les signatures a dins l'aplicació de custòdia de la CAIB.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class CustodiaPluginCaib implements CustodiaPlugin {

	private ClienteCustodiaCaib clienteCustodia;



	public String addSignature(
			String documentId,
			String arxiuNom,
			String tipusDocument,
			byte[] signatura) throws CustodiaPluginException {
		try {
			byte[] xml = getClienteCustodia().custodiarPDFFirmado(
					new ByteArrayInputStream(signatura),
					arxiuNom,
					documentId,
					tipusDocument);
			CustodiaResponseCaib resposta = getClienteCustodia().parseResponse(xml);
			if (resposta.isError())
				throw new CustodiaPluginException("Error en la petició de custòdia: [" + resposta.getErrorCodi() + "] " + resposta.getErrorDescripcio());
			return documentId;
		} catch (Exception ex) {
			logger.error("No s'ha pogut custodiar la signatura", ex);
			throw new CustodiaPluginException("No s'ha pogut custodiar la signatura", ex);
		}
	}

	public List<byte[]> getSignatures(String id) throws CustodiaPluginException {
		try {
			byte[] consultar = getClienteCustodia().consultarDocumento(id);
			byte[] iniciXml = new byte[5];
			for (int i = 0; i < 5; i++)
				iniciXml[i] = consultar[i];
			if ("<?xml".equals(new String(iniciXml))) {
				CustodiaResponseCaib resposta = getClienteCustodia().parseResponse(consultar);
				throw new CustodiaPluginException("Error en la petició de custòdia: [" + resposta.getErrorCodi() + "] " + resposta.getErrorDescripcio());
			} else {
				List<byte[]> resposta = new ArrayList<byte[]>();
				resposta.add(consultar);
				return resposta;
			}
		} catch (Exception ex) {
			logger.error("No s'han pogut obtenir les signatures", ex);
			throw new CustodiaPluginException("No s'han pogut obtenir les signatures", ex);
		}
	}

	public void deleteSignatures(String id) throws CustodiaPluginException {
		try {
			byte[] xml = getClienteCustodia().eliminarDocumento(id);
			CustodiaResponseCaib resposta = getClienteCustodia().parseResponse(xml);
			if (resposta.isError())
				throw new CustodiaPluginException("Error en la petició de custòdia: [" + resposta.getErrorCodi() + "] " + resposta.getErrorDescripcio());
		} catch (Exception ex) {
			logger.error("No s'han pogut esborrar les signatures", ex);
			throw new CustodiaPluginException("No s'han pogut esborrar les signatures", ex);
		}
	}

	public List<InfoSignatura> infoSignatures(String id) throws CustodiaPluginException {
		try {
			byte[] xml = getClienteCustodia().verificarDocumento(id);
			CustodiaResponseCaib resposta = getClienteCustodia().parseResponse(xml);
			if (resposta.isError())
				throw new CustodiaPluginException("Error en la petició de custòdia: [" + resposta.getErrorCodi() + "] " + resposta.getErrorDescripcio());
			return parseSignatures(xml);
		} catch (Exception ex) {
			logger.error("No s'han pogut verificar les signatures", ex);
			throw new CustodiaPluginException("No s'han pogut verificar les signatures", ex);
		}
	}

	public boolean potObtenirInfoSignatures() {
		return true;
	}
	public boolean isValidacioImplicita() {
		return true;
	}



	private ClienteCustodiaCaib getClienteCustodia() {
		if (clienteCustodia == null) {
			clienteCustodia = new ClienteCustodiaCaib(
					GlobalProperties.getInstance().getProperty("app.custodia.plugin.caib.url"),
					GlobalProperties.getInstance().getProperty("app.custodia.plugin.caib.usuari"),
					GlobalProperties.getInstance().getProperty("app.custodia.plugin.caib.password"));
		}
		return clienteCustodia;
	}

	/*private CustodiaResponseCaib parseResponse(byte[] response) throws DocumentException {
		CustodiaResponseCaib resposta = new CustodiaResponseCaib();
		Document document = DocumentHelper.parseText(new String(response));
		Element resultMajorElement = null;
		Element resultMinorElement = null;
		Element resultMessageElement = null;
		if ("CustodiaResponse".equals(document.getRootElement().getName())) {
			resultMajorElement = document.getRootElement().element("VerifyResponse").element("Result").element("ResultMajor");
			resultMinorElement = document.getRootElement().element("VerifyResponse").element("Result").element("ResultMinor");
			resultMessageElement = document.getRootElement().element("VerifyResponse").element("Result").element("ResultMessage");
		} else if ("EliminacionResponse".equals(document.getRootElement().getName())) {
			resultMajorElement = document.getRootElement().element("Result").element("ResultMajor");
			resultMinorElement = document.getRootElement().element("Result").element("ResultMinor");
			resultMessageElement = document.getRootElement().element("Result").element("ResultMessage");
		} else if ("ConsultaResponse".equals(document.getRootElement().getName())) {
			resultMajorElement = document.getRootElement().element("Result").element("ResultMajor");
			resultMinorElement = document.getRootElement().element("Result").element("ResultMinor");
			resultMessageElement = document.getRootElement().element("Result").element("ResultMessage");
		} else if ("VerificacionResponse".equals(document.getRootElement().getName())) {
			resultMajorElement = document.getRootElement().element("Result").element("ResultMajor");
			resultMinorElement = document.getRootElement().element("Result").element("ResultMinor");
			resultMessageElement = document.getRootElement().element("Result").element("ResultMessage");
		}
		if (resultMajorElement == null)
			throw new DocumentException("No s'ha trobat el ResultMajor");
		boolean hasErrors = "RequesterError".equals(resultMajorElement.getText());
		resposta.setError(hasErrors);
		//System.out.println(">>> " + resultMajorElement.getText() + ":" + resultMinorElement.getText() + ":" + resultMessageElement.getText());
		if (hasErrors) {
			resposta.setErrorCodi(resultMinorElement.getText());
			resposta.setErrorDescripcio(resultMessageElement.getText());
		}
		return resposta;
	}*/

	@SuppressWarnings("unchecked")
	private List<InfoSignatura> parseSignatures(byte[] response) throws DocumentException {
		List<InfoSignatura> resposta = new ArrayList<InfoSignatura>();
		Document document = DocumentHelper.parseText(new String(response));
		Element resultadoFirmas = document.getRootElement().element("VerificacionUltimaCustodia").element("ResultadoFirmas");
		Iterator<Element> it = (Iterator<Element>)resultadoFirmas.elementIterator("ResultadoFirma");
		while (it.hasNext()) {
			Element element = it.next();
			Element certInfo = element.element("ValidacionCertificado");
			boolean verificada = "true".equalsIgnoreCase(certInfo.element("verificado").getText());
			//String certNumSerie = certInfo.element("numeroSerie").getText();
			String certSubject = certInfo.element("subjectName").getText();
			InfoSignatura infoSignatura = new InfoSignatura(null);
			infoSignatura.setValida(verificada);
			InfoCertificat infoCertificat = new InfoCertificat();
			infoCertificat.setFullName(getCertSubjectToken(certSubject, "CN="));
			infoCertificat.setGivenName(getCertSubjectToken(certSubject, "GIVENNAME="));
			infoCertificat.setSurname(getCertSubjectToken(certSubject, "SURNAME="));
			infoCertificat.setNif(getCertSubjectToken(certSubject, "SERIALNUMBER="));
			infoCertificat.setEmail(getCertSubjectToken(certSubject, ",E="));
			infoSignatura.setInfoCertificat(infoCertificat);
			resposta.add(infoSignatura);
		}
		return resposta;
	}
	private String getCertSubjectToken(String certSubject, String token) {
		int indexInici = certSubject.indexOf(token);
		int indexFi = certSubject.indexOf(",", indexInici);
		indexInici += token.length();
		return certSubject.substring(indexInici, indexFi);
	}

	private static final Log logger = LogFactory.getLog(CustodiaPluginCaib.class);

}
