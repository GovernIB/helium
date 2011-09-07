/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.custodia;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import es.caib.signatura.cliente.custodia.CustodiaRequestBuilder;
import es.caib.signatura.cliente.services.custodia.CustodiaService;
import es.caib.signatura.cliente.services.custodia.CustodiaServiceLocator;
import es.caib.signatura.cliente.services.custodia.CustodiaSoapBindingStub;
import es.caib.signatura.cliente.services.custodia.Custodia_PortType;

/**
 * Client per a l'aplicació de custòdia documental de la CAIB
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClienteCustodiaCaib {

	private String url;
	private String usuario;
	private String password;

	private CustodiaService service = null;
	private Custodia_PortType custodiaPort = null;

	public ClienteCustodiaCaib(String url, String usuario, String password) {
		this.url = url;
		this.usuario = usuario;
		this.password = password;
	}

	public byte[] custodiarDocumentoSMIME(
			ByteArrayInputStream documento,
			String nombreDocumento,
			String codigoExterno,
			String codigoExternoTipoDocumento) throws RemoteException {
		init();
		CustodiaRequestBuilder custodiaRequestBuilder = new CustodiaRequestBuilder(
				this.usuario,
				this.password);
		byte[] xml = custodiaRequestBuilder.buildXML(documento, nombreDocumento, codigoExterno, codigoExternoTipoDocumento);
		return this.custodiaPort.custodiarDocumentoSMIME_v2(xml);
	}

	public byte[] custodiarDocumento(
			ByteArrayInputStream documento,
			String nombreDocumento,
			String codigoExterno,
			String codigoExternoTipoDocumento) throws RemoteException {
		init();
		CustodiaRequestBuilder custodiaRequestBuilder = new CustodiaRequestBuilder(
				this.usuario,
				this.password);
		byte[] xml = custodiaRequestBuilder.buildXML(documento, nombreDocumento, codigoExterno, codigoExternoTipoDocumento);
		return this.custodiaPort.custodiarDocumento_v2(xml);
	}

	public byte[] custodiarPDFFirmado(
			ByteArrayInputStream documento,
			String nombreDocumento,
			String codigoExterno,
			String codigoExternoTipoDocumento) throws RemoteException {
		init();
		CustodiaRequestBuilder custodiaRequestBuilder = new CustodiaRequestBuilder(
				this.usuario,
				this.password);
		byte[] xml = custodiaRequestBuilder.buildXML(documento, nombreDocumento, codigoExterno, codigoExternoTipoDocumento);
		return this.custodiaPort.custodiarPDFFirmado_v2(xml);
	}

	public byte[] purgarDocumento(String codigoExterno) throws RemoteException {
		init();
		return this.custodiaPort.purgarDocumento_v2(
				this.usuario,
				this.password,
				codigoExterno);
	}

	public byte[] recuperarDocumento(String codigoExterno) throws RemoteException {
		init();
		return this.custodiaPort.recuperarDocumento_v2(
				this.usuario,
				this.password,
				codigoExterno);
	}

	public byte[] eliminarDocumento(String codigoExterno) throws RemoteException {
		init();
		return this.custodiaPort.eliminarDocumento_v2(
				this.usuario,
				this.password,
				codigoExterno);
	}

	public byte[] verificarDocumento(String codigoExterno) throws RemoteException {
		init();
		return this.custodiaPort.verificarDocumento_v2(
				this.usuario,
				this.password,
				codigoExterno);
	}

	public byte[] obtenerInformeDocumento(String codigoExterno) throws RemoteException {
		init();
		return this.custodiaPort.obtenerInformeDocumento_v2(
				this.usuario,
				this.password,
				codigoExterno);
	}

	public byte[] consultarDocumento(String codigoExterno) throws RemoteException {
		init();
		return this.custodiaPort.consultarDocumento_v2(
				this.usuario,
				this.password,
				codigoExterno);
	}

	public byte[] reservarDocumento(String codigoExterno) throws RemoteException {
		init();
		return this.custodiaPort.reservarDocumento(
				this.usuario,
				this.password,
				codigoExterno);
	}

	public byte[] consultarReservaDocumento(String hash) throws RemoteException {
		init();
		return this.custodiaPort.consultarReservaDocumento(
				this.usuario,
				this.password,
				hash);
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



	private void init() {
		try {
			if (custodiaPort == null) {
				service = new CustodiaServiceLocator();
				custodiaPort = service.getCustodia(new URL(url));
				((CustodiaSoapBindingStub)custodiaPort).setUsername(usuario);
				((CustodiaSoapBindingStub)custodiaPort).setPassword(password);
			}
		} catch (ServiceException sex) {
			sex.printStackTrace();
		} catch (MalformedURLException mux) {
			mux.printStackTrace();
		}
	}

	private boolean hasErrors(String resultMajor) {
		return (resultMajor.contains("error") ||
				resultMajor.contains("Error") ||
				resultMajor.contains("ERROR"));
	}

}
