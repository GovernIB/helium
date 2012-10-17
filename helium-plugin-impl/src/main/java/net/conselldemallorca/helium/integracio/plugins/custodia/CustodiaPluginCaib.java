/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.custodia;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.signatura.DadesCertificat;
import net.conselldemallorca.helium.integracio.plugins.signatura.RespostaValidacioSignatura;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Implementació del plugin de custodia documental que guarda
 * les signatures a dins l'aplicació de custòdia de la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CustodiaPluginCaib implements CustodiaPlugin {

	private ClienteCustodiaCaib clienteCustodia;
	private Map<String, String> cacheHash = new HashMap<String, String>();



	public String addSignature(
			String id,
			String gesdocId,
			String arxiuNom,
			String tipusDocument,
			byte[] signatura) throws CustodiaPluginException {
		try {
			String custodiaId = getIdCustodia(id);
			byte[] xml = getClienteCustodia().custodiarPDFFirmado(
					new ByteArrayInputStream(signatura),
					arxiuNom,
					custodiaId,
					tipusDocument);
			CustodiaResponseCaib resposta = getClienteCustodia().parseResponse(xml);
			if (resposta.isError())
				throw new CustodiaPluginException("Error en la petició de custòdia: [" + resposta.getErrorCodi() + "] " + resposta.getErrorDescripcio());
			return custodiaId;
		} catch (Exception ex) {
			//logger.error("No s'ha pogut custodiar la signatura: " + ex.getMessage());
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
			//logger.error("No s'han pogut obtenir les signatures: " + ex.getMessage());
			throw new CustodiaPluginException("No s'han pogut obtenir les signatures", ex);
		}
	}

	public byte[] getSignaturesAmbArxiu(String id) throws CustodiaPluginException {
		try {
			byte[] consultar = getClienteCustodia().consultarDocumento(id);
			byte[] iniciXml = new byte[5];
			for (int i = 0; i < 5; i++)
				iniciXml[i] = consultar[i];
			if ("<?xml".equals(new String(iniciXml))) {
				CustodiaResponseCaib resposta = getClienteCustodia().parseResponse(consultar);
				throw new CustodiaPluginException("Error en la petició de custòdia: [" + resposta.getErrorCodi() + "] " + resposta.getErrorDescripcio());
			} else {
				return consultar;
			}
		} catch (Exception ex) {
			//logger.error("No s'ha pogut obtenir l'arxiu amb les signatures: " + ex.getMessage());
			throw new CustodiaPluginException("No s'ha pogut obtenir l'arxiu amb les signatures", ex);
		}
	}

	public void deleteSignatures(String id) throws CustodiaPluginException {
		try {
			byte[] xml = getClienteCustodia().eliminarDocumento(id);
			CustodiaResponseCaib resposta = getClienteCustodia().parseResponse(xml);
			if (resposta.isError())
				throw new CustodiaPluginException("Error en la petició de custòdia: [" + resposta.getErrorCodi() + "] " + resposta.getErrorDescripcio());
		} catch (Exception ex) {
			//logger.error("No s'han pogut esborrar les signatures: " + ex.getMessage());
			throw new CustodiaPluginException("No s'han pogut esborrar les signatures", ex);
		}
	}

	public List<RespostaValidacioSignatura> dadesValidacioSignatura(String id) throws CustodiaPluginException {
		try {
			byte[] xml = getClienteCustodia().verificarDocumento(id);
			CustodiaResponseCaib resposta = getClienteCustodia().parseResponse(xml);
			if (resposta.isError())
				throw new CustodiaPluginException("Error en la petició de custòdia: [" + resposta.getErrorCodi() + "] " + resposta.getErrorDescripcio());
			return parseSignatures(xml);
		} catch (Exception ex) {
			//logger.error("No s'han pogut verificar les signatures: " + ex.getMessage());
			throw new CustodiaPluginException("No s'han pogut verificar les signatures", ex);
		}
	}

	public boolean potObtenirInfoSignatures() {
		return true;
	}
	public boolean isValidacioImplicita() {
		return true;
	}

	public String getUrlComprovacioSignatura(
			String id) throws CustodiaPluginException {
		try {
			String token = cacheHash.get(id);
			if (token == null) {
				byte[] resposta = getClienteCustodia().reservarDocumento(getIdCustodia(id));
				token = new String(resposta);
				cacheHash.put(id, token);
			}
			String baseUrl = GlobalProperties.getInstance().getProperty("app.custodia.plugin.caib.verificacio.baseurl");
			return baseUrl + token;
		} catch (Exception ex) {
			//logger.error("No s'ha pogut generar la url de comprovació de signatura: " + ex.getMessage());
			throw new CustodiaPluginException("No s'ha pogut generar la url de comprovació de signatura", ex);
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
	private List<RespostaValidacioSignatura> parseSignatures(byte[] response) throws DocumentException {
		List<RespostaValidacioSignatura> resposta = new ArrayList<RespostaValidacioSignatura>();
		Document document = DocumentHelper.parseText(new String(response));
		Element resultadoFirmas = document.getRootElement().element("VerificacionUltimaCustodia").element("ResultadoFirmas");
		Iterator<Element> it = (Iterator<Element>)resultadoFirmas.elementIterator("ResultadoFirma");
		while (it.hasNext()) {
			Element element = it.next();
			Element certInfo = element.element("ValidacionCertificado");
			boolean verificada = "true".equalsIgnoreCase(certInfo.element("verificado").getText());
			RespostaValidacioSignatura res = new RespostaValidacioSignatura();
			if (verificada)
				res.setEstat(RespostaValidacioSignatura.ESTAT_OK);
			else
				res.setEstat(RespostaValidacioSignatura.ESTAT_ERROR);
			//String certNumSerie = certInfo.element("numeroSerie").getText();
			String certSubject = certInfo.element("subjectName").getText();
			DadesCertificat dadesCertificat = new DadesCertificat();
			//dadesCertificat.setTipoCertificado();
			//dadesCertificat.setSubject();
			//dadesCertificat.setNombreResponsable(getCertSubjectToken(certSubject, "CN="));
			dadesCertificat.setNombreResponsable(getCertSubjectToken(certSubject, "GIVENNAME="));
			String cognoms = getCertSubjectToken(certSubject, "SURNAME=");
			String[] parts = cognoms.split(" ");
			if (parts.length == 1) {
				dadesCertificat.setPrimerApellidoResponsable(parts[0]);
			} else {
				StringBuilder sb = new StringBuilder();
				for (int i = 1; i < parts.length; i++) {
					sb.append(parts[i]);
					sb.append(" ");
				}
				dadesCertificat.setSegundoApellidoResponsable(sb.toString());
			}
			dadesCertificat.setNifResponsable(getCertSubjectToken(certSubject, "SERIALNUMBER="));
			//dadesCertificat.setIdEmisor();
			//dadesCertificat.setNifCif();
			dadesCertificat.setEmail(getCertSubjectToken(certSubject, ",E="));
			//dadesCertificat.setFechaNacimiento();
			//dadesCertificat.setRazonSocial();
			//dadesCertificat.setClasificacion();
			//dadesCertificat.setNumeroSerie();
			List<DadesCertificat> dc = new ArrayList<DadesCertificat>();
			dc.add(dadesCertificat);
			res.setDadesCertificat(dc);
			resposta.add(res);
		}
		return resposta;
	}
	private String getCertSubjectToken(String certSubject, String token) {
		int indexInici = certSubject.indexOf(token);
		int indexFi = certSubject.indexOf(",", indexInici + token.length());
		indexInici += token.length();
		return certSubject.substring(indexInici, indexFi);
	}

	private String getIdCustodia(String docId) {
		return docId;
	}

}
