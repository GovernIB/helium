package net.conselldemallorca.helium.test.integracio;


import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.conselldemallorca.helium.integracio.plugins.custodia.CustodiaPluginException;
import net.conselldemallorca.helium.integracio.plugins.custodia.CustodiaResponseCaib;
import net.conselldemallorca.helium.test.util.BaseTest;
import net.conselldemallorca.helium.wsintegraciones.custodiadocumentos.cliente.CustodiaDocumentosSoapBindingStub;
import net.conselldemallorca.helium.wsintegraciones.custodiadocumentos.cliente.CustodiaServiceLocator;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import es.caib.signatura.cliente.custodia.CustodiaRequestBuilder;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Custodia extends BaseTest {
	private CustodiaDocumentosSoapBindingStub clienteCustodia;
	
	private Map<String, String> cacheHash = new HashMap<String, String>();

	@BeforeClass
	public static void beforeClass() {
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.Log4JLogger");
		System.setProperty("org.apache.commons.logging.LogFactory", "org.apache.commons.logging.impl.LogFactoryImpl");
	}

	@Test
	public void a_getUrlComprovacioCustodia() throws CustodiaPluginException {
		try {
			String token = cacheHash.get("1");
			if (token == null) {
				byte[] resposta = getClienteCustodia().reservarDocumento_v2("1", "2", "3");
				token = new String(resposta);
				cacheHash.put("1", token);
			}
			String baseUrl = properties.getProperty("app.custodia.plugin.caib.verificacio.baseurl");
			assertTrue("Error al comprobar la URL del plugin de custodia: " + (baseUrl + token), !(baseUrl + token).isEmpty());
		} catch (Exception ex) {
			throw new CustodiaPluginException("No s'ha pogut generar la url de comprovació de custodia", ex);
		}
	}
	
	@Test
	public void b_custodiarDocumento() throws Exception {
		CustodiaRequestBuilder custodiaRequestBuilder = new CustodiaRequestBuilder(
				properties.getProperty("app.custodia.plugin.caib.usuari"),
				properties.getProperty("app.custodia.plugin.caib.password"));
		String filename = carregarPropietatPath("deploy.arxiu.custodia", "No se encontró el fichero a enviar al custodia");
		byte[] xmlRequest = custodiaRequestBuilder.buildXML(new ByteArrayInputStream(IOUtils.toByteArray(new FileInputStream(filename))), "original.pdf",
				"3500",
				"HELIUM_COMINF_DOCAPRO1");
		byte[] xmlResponse = getClienteCustodia().custodiarDocumento_v2(xmlRequest);
		
		CustodiaResponseCaib resposta = parseResponse(xmlResponse);
		if (resposta.isError())
			throw new CustodiaPluginException("Error en la petició de custòdia: [" + resposta.getErrorCodi() + "] " + resposta.getErrorDescripcio());
		assertFalse("Error en la petició de custòdia", resposta.isError());
	}
	
	@Test
	public void c_consultarDocumento() throws Exception {
		byte[] xmlResponse = getClienteCustodia().consultarDocumento_v2("a", "b", "c");
		
		CustodiaResponseCaib resposta = parseResponse(xmlResponse);
		if (resposta.isError())
			throw new CustodiaPluginException("Error en la petició de custòdia: [" + resposta.getErrorCodi() + "] " + resposta.getErrorDescripcio());
		assertFalse("Error en la petició de custòdia", resposta.isError());
	}

	public CustodiaResponseCaib parseResponse(byte[] response) throws DocumentException {
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
		boolean hasErrors = hasErrors(resultMajorElement.getText());
		resposta.setError(hasErrors);
		if (hasErrors) {
			resposta.setErrorCodi(resultMinorElement.getText());
			resposta.setErrorDescripcio(resultMessageElement.getText());
		}
		return resposta;
	}

	private boolean hasErrors(String resultMajor) {
		return (resultMajor.contains("error") ||
				resultMajor.contains("Error") ||
				resultMajor.contains("ERROR"));
	}

	private CustodiaDocumentosSoapBindingStub getClienteCustodia() {
		if (clienteCustodia == null) {
			try {
				String urlEndPoint = properties.getProperty("app.custodia.plugin.caib.url");
				CustodiaServiceLocator service = new CustodiaServiceLocator(); 
				clienteCustodia = (CustodiaDocumentosSoapBindingStub) service.getCustodiaDocumentos(new URL(urlEndPoint));
				clienteCustodia.setTimeout(100000);
			} catch (Exception e) {
				return null;
			} 
		}
		return clienteCustodia;
	}
}
