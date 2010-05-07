/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.custodia;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.conselldemallorca.helium.model.exception.CustodiaPluginException;
import net.conselldemallorca.helium.util.GlobalProperties;

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



	public String addSignedDocument(DocumentCustodia document) {
		try {
			/*FileOutputStream fout = new FileOutputStream("c:/docsignat.pdf");
			fout.write((byte[])document.getSignature());
			fout.close();*/
			byte[] xml = getClienteCustodia().custodiarPDFFirmado(
					new ByteArrayInputStream((byte[])document.getSignature()),
					document.getSignedFileName(),
					document.getId(),
					document.getCustodiaCodi());
			CustodiaResponse resposta = parseResponse(xml);
			if (!resposta.isError())
				return document.getId();
			else
				throw new CustodiaPluginException("Error en la petició de custòdia: [" + resposta.getErrorCodi() + "] " + resposta.getErrorDescripcio());
		} catch (Exception ex) {
			throw new CustodiaPluginException("No s'ha pogut guardar el document", ex);
		}
	}

	public void deleteSignedDocument(String id) {
		try {
			byte[] xml = getClienteCustodia().eliminarDocumento(id);
			CustodiaResponse resposta = parseResponse(xml);
			if (resposta.isError())
				throw new CustodiaPluginException("Error en la petició de custòdia: [" + resposta.getErrorCodi() + "] " + resposta.getErrorDescripcio());
		} catch (Exception ex) {
			throw new CustodiaPluginException("No s'ha pogut esborrar el document", ex);
		}
	}

	public DocumentCustodia getSignedDocument(String id) {
		try {
			byte[] consultar = getClienteCustodia().consultarDocumento(id);
			byte[] iniciXml = new byte[5];
			for (int i = 0; i < 5; i++)
				iniciXml[i] = consultar[i];
			if ("<?xml".equals(new String(iniciXml))) {
				CustodiaResponse resposta = parseResponse(consultar);
				throw new CustodiaPluginException("Error en la petició de custòdia: [" + resposta.getErrorCodi() + "] " + resposta.getErrorDescripcio());
			} else {
				DocumentCustodia resposta = new DocumentCustodia();
				resposta.setSignedFileName("signat.pdf");
				resposta.setSignedFileContent(consultar);
				return resposta;
			}
		} catch (Exception ex) {
			throw new CustodiaPluginException("No s'ha pogut recuperar el document", ex);
		}
	}

	public List<SignaturaInfo> verifyDocument(String id) {
		try {
			byte[] xml = getClienteCustodia().verificarDocumento(id);
			CustodiaResponse resposta = parseResponse(xml);
			if (resposta.isError())
				throw new CustodiaPluginException("Error en la petició de custòdia: [" + resposta.getErrorCodi() + "] " + resposta.getErrorDescripcio());
			return parseSignatures(xml);
		} catch (Exception ex) {
			throw new CustodiaPluginException("No s'ha pogut esborrar el document", ex);
		}
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

	private CustodiaResponse parseResponse(byte[] response) throws DocumentException {
		CustodiaResponse resposta = new CustodiaResponse();
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
	}

	@SuppressWarnings("unchecked")
	private List<SignaturaInfo> parseSignatures(byte[] response) throws DocumentException {
		List<SignaturaInfo> resposta = new ArrayList<SignaturaInfo>();
		Document document = DocumentHelper.parseText(new String(response));
		Element resultadoFirmas = document.getRootElement().element("VerificacionUltimaCustodia").element("ResultadoFirmas");
		Iterator<Element> it = (Iterator<Element>)resultadoFirmas.elementIterator("ResultadoFirma");
		while (it.hasNext()) {
			Element element = it.next();
			SignaturaInfo info = new SignaturaInfo();
			info.setNivell(Integer.parseInt(element.element("nivel").getText()));
			info.setOrdre(Integer.parseInt(element.element("orden").getText()));
			info.setVerificada("true".equalsIgnoreCase(element.element("verificada").getText()));
			Element certInfo = element.element("ValidacionCertificado");
			info.setCertNumSerie(certInfo.element("numeroSerie").getText());
			info.setCertSubject(certInfo.element("subjectName").getText());
			info.setCertVerificat("true".equalsIgnoreCase(certInfo.element("verificado").getText()));
			resposta.add(info);
		}
		return resposta;
	}

}
