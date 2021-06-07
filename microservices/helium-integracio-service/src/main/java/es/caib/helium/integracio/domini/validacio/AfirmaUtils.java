package es.caib.helium.integracio.domini.validacio;

import java.util.ArrayList;
import java.util.List;

//TODO  S'ha esborrat el javax.xml.namespace de la llibreria axis/axis-jaxrpc sino genera conflicte ja que java11 la porta el jre
import javax.xml.namespace.QName; 
//-----------------------------------------------------------------------------------------------

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

import es.caib.helium.integracio.excepcions.validacio.ValidacioFirmaException;
import es.caib.helium.integracio.service.validacio.AfirmaSecurityHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AfirmaUtils {
	
	public static final String MODE_VALIDACIO_SIMPLE = "0";
	public static final String MODE_VALIDACIO_AMB_REVOCACIO = "1";
	public static final String MODE_VALIDACIO_CADENA = "2";

	public static final String FORMAT_SIGNATURA_PKCS7 = "PKCS7";
	public static final String FORMAT_SIGNATURA_CMS = "CMS";
	public static final String FORMAT_SIGNATURA_CADES_BES = "CAdES-BES";
	public static final String FORMAT_SIGNATURA_CADES_T = "CAdES-T";
	public static final String FORMAT_SIGNATURA_XMLDSIG = "XMLDSIG";
	public static final String FORMAT_SIGNATURA_XADES = "XAdES";
	public static final String FORMAT_SIGNATURA_XADES_BES = "XAdES-BES";
	public static final String FORMAT_SIGNATURA_XADES_T = "XAdES-T";
	public static final String FORMAT_SIGNATURA_PDF = "PDF";
	public static final String FORMAT_SIGNATURA_ODF = "ODF";

	private String baseUrl;
	private String aplicacioId;
	private String usuari;
	private String password;
	private String formatSignatura;
	private String modeValidacio;

	private boolean logMissatges;

	public AfirmaUtils(
			String baseUrl,
			String aplicacioId) {
		this.baseUrl = baseUrl;
		this.aplicacioId = aplicacioId;
	}
	public AfirmaUtils(
			String baseUrl,
			String aplicacioId,
			String usuari,
			String password) {
		this.baseUrl = baseUrl;
		this.aplicacioId = aplicacioId;
		this.usuari = usuari;
		this.password = password;
	}

	public ValidarCertificatResponse validarCertificat(String certificatBase64, boolean obtenirDadesCertificat) throws ValidacioFirmaException {
		
		try {
			String respostaXml = cridarValidarCertificado(
					certificatBase64,
					obtenirDadesCertificat);
			ValidarCertificatResponse resposta = new ValidarCertificatResponse();
			Document document = obtenirDocumentXmlResposta(respostaXml);
			Node resultado = document.selectSingleNode("/mensajeSalida/respuesta/ResultadoProcesamiento/ResultadoValidacion/resultado");
			if (resultado != null && "0".equals(resultado.getText())) {
				resposta.setEstat(ValidarSignaturaResponse.ESTAT_OK);
				//Node descripcion = document.selectSingleNode("/mensajeSalida/respuesta/ResultadoProcesamiento/ResultadoValidacion/descripcion");
				if (obtenirDadesCertificat) {
					Node infoCertificado = document.selectSingleNode("/mensajeSalida/respuesta/ResultadoProcesamiento/InfoCertificado");
					resposta.setDadesCertificat(getDadesCertificat(infoCertificado));
				}
			} else {
				resposta.setEstat(ValidarSignaturaResponse.ESTAT_ERROR);
				Node codigoError = document.selectSingleNode("/mensajeSalida/respuesta/Excepcion/codigoError");
				Node descripcionError = document.selectSingleNode("/mensajeSalida/respuesta/Excepcion/descripcion");
				Node excepcionAsociada = document.selectSingleNode("/mensajeSalida/respuesta/Excepcion/excepcionAsociada");
				if (codigoError != null)
					resposta.setErrorCodi(codigoError.getText());
				if (descripcionError != null)
					resposta.setErrorDescripcio(descripcionError.getText());
				if (excepcionAsociada != null)
					resposta.setErrorExcepcio(excepcionAsociada.getText());
			}
			return resposta;
		} catch (Exception ex) {
			throw new ValidacioFirmaException("Error en la validació de certificat", ex);
		}
	}

	public ObtenirDadesCertificatResponse obtenirDadesCertificat(String certificatBase64) throws ValidacioFirmaException {
		
		try {
			String respostaXml = cridarInfoCertificado(
					certificatBase64);
			ObtenirDadesCertificatResponse resposta = new ObtenirDadesCertificatResponse();
			Document document = obtenirDocumentXmlResposta(respostaXml);
			Node infoCertificado = document.selectSingleNode("/mensajeSalida/respuesta/ResultadoProcesamiento/InfoCertificado");
			if (infoCertificado != null) {
				resposta.setEstat(ValidarSignaturaResponse.ESTAT_OK);
				resposta.setDadesCertificat(getDadesCertificat(infoCertificado));
			} else {
				resposta.setEstat(ValidarSignaturaResponse.ESTAT_ERROR);
				Node codigoError = document.selectSingleNode("/mensajeSalida/respuesta/Excepcion/codigoError");
				Node descripcionError = document.selectSingleNode("/mensajeSalida/respuesta/Excepcion/descripcion");
				Node excepcionAsociada = document.selectSingleNode("/mensajeSalida/respuesta/Excepcion/excepcionAsociada");
				if (codigoError != null) {
					resposta.setErrorCodi(codigoError.getText());
				}
				if (descripcionError != null) {
					resposta.setErrorDescripcio(descripcionError.getText());
				}
				if (excepcionAsociada != null) {
					resposta.setErrorExcepcio(excepcionAsociada.getText());
				}
			}
			return resposta;
		} catch (Exception ex) {
			throw new ValidacioFirmaException("Error en la obtenció de dades del certificat", ex);
		}
	}

	@SuppressWarnings("unchecked")
	public ValidarSignaturaResponse validarSignatura(String documentBase64, String signaturaBase64, boolean obtenirDadesCertificat) throws ValidacioFirmaException {
		
		try {
			String respostaXml = cridarValidarFirma(
					signaturaBase64,
					documentBase64);
			ValidarSignaturaResponse resposta = new ValidarSignaturaResponse();
			Document document = obtenirDocumentXmlResposta(respostaXml);
			Node estado = document.selectSingleNode("/mensajeSalida/respuesta/Respuesta/estado");
			if (estado != null && "true".equalsIgnoreCase(estado.getText())) {
				resposta.setEstat(ValidarSignaturaResponse.ESTAT_OK);
				//Node proceso = document.selectSingleNode("/mensajeSalida/respuesta/Respuesta/descripcion/validacionFirmaElectronica/proceso");
				//Node detalle = document.selectSingleNode("/mensajeSalida/respuesta/Respuesta/descripcion/validacionFirmaElectronica/detalle");
				//Node conclusion = document.selectSingleNode("/mensajeSalida/respuesta/Respuesta/descripcion/validacionFirmaElectronica/conclusion");
				List<Node> certificats = document.selectNodes("/mensajeSalida/respuesta/Respuesta/descripcion/validacionFirmaElectronica/informacionAdicional/firmante/certificado");
				if (obtenirDadesCertificat && certificats != null) {
					List<DadesCertificat> dadesCertificat = new ArrayList<DadesCertificat>();
					for (Node certificat: certificats) {
						ObtenirDadesCertificatResponse resp = obtenirDadesCertificat(
								certificat.getText());
						if (resp.isEstatOk()) {
							dadesCertificat.add(resp.getDadesCertificat());
						}
					}
					resposta.setDadesCertificat(dadesCertificat);
				}
			} else {
				resposta.setEstat(ValidarSignaturaResponse.ESTAT_ERROR);
				Node codigoError = document.selectSingleNode("/mensajeSalida/respuesta/Excepcion/codigoError");
				Node descripcionError = document.selectSingleNode("/mensajeSalida/respuesta/Excepcion/descripcion");
				Node excepcionAsociada = document.selectSingleNode("/mensajeSalida/respuesta/Excepcion/excepcionAsociada");
				if (codigoError != null) {
					resposta.setErrorCodi(codigoError.getText());
				}
				if (descripcionError != null) {
					resposta.setErrorDescripcio(descripcionError.getText());
				}
				if (excepcionAsociada != null) {
					resposta.setErrorExcepcio(excepcionAsociada.getText());
				}
			}
			return resposta;
		} catch (Exception ex) {
			throw new ValidacioFirmaException("Error en la validació de signatura", ex);
		}
	}



	public String getBaseUrl() {
		return baseUrl;
	}
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	public String getAplicacioId() {
		return aplicacioId;
	}
	public void setAplicacioId(String aplicacioId) {
		this.aplicacioId = aplicacioId;
	}
	public String getUsuari() {
		return usuari;
	}
	public void setUsuari(String usuari) {
		this.usuari = usuari;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFormatSignatura() {
		return formatSignatura;
	}
	public void setFormatSignatura(String formatSignatura) {
		this.formatSignatura = formatSignatura;
	}
	public String getModeValidacio() {
		return modeValidacio;
	}
	public void setModeValidacio(String modeValidacio) {
		this.modeValidacio = modeValidacio;
	}
	public boolean isLogMissatges() {
		return logMissatges;
	}
	public void setLogMissatges(boolean logMissatges) {
		this.logMissatges = logMissatges;
	}



	@SuppressWarnings("unchecked")
	private DadesCertificat getDadesCertificat(
			Node infoCertificado) throws Exception {
		if (infoCertificado == null)
			return null;
		DadesCertificat dades = new DadesCertificat();
		List<Node> llistaIds = infoCertificado.selectNodes("Campo/idCampo");
		List<Node> llistaValors = infoCertificado.selectNodes("Campo/valorCampo");
		for (int i = 0; i < llistaIds.size(); i++) {
			if ("tipoCertificado".equalsIgnoreCase(llistaIds.get(i).getText()))
				dades.setTipoCertificado(llistaValors.get(i).getText());
			if ("subject".equalsIgnoreCase(llistaIds.get(i).getText()))
				dades.setSubject(llistaValors.get(i).getText());
			if ("nombreResponsable".equalsIgnoreCase(llistaIds.get(i).getText()))
				dades.setNombreResponsable(llistaValors.get(i).getText());
			if ("primerApellidoResponsable".equalsIgnoreCase(llistaIds.get(i).getText()))
				dades.setPrimerApellidoResponsable(llistaValors.get(i).getText());
			if ("segundoApellidoResponsable".equalsIgnoreCase(llistaIds.get(i).getText()))
				dades.setSegundoApellidoResponsable(llistaValors.get(i).getText());
			if ("NIFResponsable".equalsIgnoreCase(llistaIds.get(i).getText()))
				dades.setNifResponsable(llistaValors.get(i).getText());
			if ("idEmisor".equalsIgnoreCase(llistaIds.get(i).getText()))
				dades.setIdEmisor(llistaValors.get(i).getText());
			if ("NIF-CIF".equalsIgnoreCase(llistaIds.get(i).getText()))
				dades.setNifCif(llistaValors.get(i).getText());
			if ("email".equalsIgnoreCase(llistaIds.get(i).getText()))
				dades.setEmail(llistaValors.get(i).getText());
			if ("fechaNacimiento".equalsIgnoreCase(llistaIds.get(i).getText()))
				dades.setFechaNacimiento(llistaValors.get(i).getText());
			if ("razonSocial".equalsIgnoreCase(llistaIds.get(i).getText()))
				dades.setRazonSocial(llistaValors.get(i).getText());
			if ("clasificacion".equalsIgnoreCase(llistaIds.get(i).getText()))
				dades.setRazonSocial(llistaValors.get(i).getText());
			if ("numeroSerie".equalsIgnoreCase(llistaIds.get(i).getText()))
				dades.setNumeroSerie(llistaValors.get(i).getText());
		}
		return dades;
	}

	private Document obtenirDocumentXmlResposta(String xmlResposta) throws DocumentException  {
		
		// Llevam tots els atributs de mensajeSalida
		int tall1 = xmlResposta.indexOf("mensajeSalida") + "mensajeSalida".length();
		int tall2 = xmlResposta.indexOf(">", tall1);
		String xmlCorrecte = xmlResposta.substring(0, tall1) + xmlResposta.substring(tall2);
		return DocumentHelper.parseText(xmlCorrecte);
	}

	private String cridarValidarCertificado(String certificatBase64, boolean obtenirDadesCertificat) throws Exception {
		
		String xmlPeticio = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<mensajeEntrada xmlns=\"http://afirmaws/ws/validacion\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mvalidacion/ws.xsd\">" +
			"<peticion>ValidarCertificado</peticion>" +
			"<versionMsg>1.0</versionMsg>" +
			"<parametros>" +
			"<certificado><![CDATA[" + certificatBase64 + "]]></certificado>" +
			"<idAplicacion>" + aplicacioId + "</idAplicacion>" +
			((modeValidacio != null) ? "<modoValidacion>" + modeValidacio + "</modoValidacion>" : "") +
			"<obtenerInfo>" + obtenirDadesCertificat + "</obtenerInfo>" +
			"</parametros>" +
			"</mensajeEntrada>";
		logSiActivat(xmlPeticio);
		Service service = new Service(); //TODO aquest codi depen d'una llibreria del 2006...
		Call call = (Call)service.createCall();
		call.setTargetEndpointAddress(baseUrl + "/ValidarCertificado");
		call.setOperationName(new QName("http://soapinterop.org/", "ValidarCertificado"));
		if ((usuari != null) && (!usuari.equals(""))) {
			try {
				call.setClientHandlers(new AfirmaSecurityHandler(usuari, password), null);
			} catch (Exception ex) {
				throw new ValidacioFirmaException("(Call) Error en la cridada a aFirma", ex);
			}
		}
		call.setReturnType(org.apache.axis.Constants.XSD_STRING);
		call.addParameter("ValidarCertificadoRequest", org.apache.axis.Constants.XSD_STRING, javax.xml.rpc.ParameterMode.IN);
		String xmlResposta = (String)call.invoke(new Object[]{xmlPeticio});
		logSiActivat(xmlResposta);
		return xmlResposta;
	}
	private String cridarInfoCertificado(String certificatBase64) throws Exception {
		
		String xmlPeticio = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<mensajeEntrada xmlns=\"http://afirmaws/ws/validacion\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchemainstance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mvalidacion/ws.xsd\">" +
			"<peticion>ObtenerInfoCertificado</peticion>" +
			"<versionMsg>1.0</versionMsg>" +
			"<parametros>" +
			"<certificado><![CDATA[" + certificatBase64 + "]]></certificado>" +
			"<idAplicacion>" + aplicacioId + "</idAplicacion>" +
			"</parametros>" +
			"</mensajeEntrada>";
		logSiActivat(xmlPeticio);
		Service service = new Service();
		Call call = (Call)service.createCall();
		call.setTargetEndpointAddress(baseUrl + "/ObtenerInfoCertificado");
		call.setOperationName(new QName("http://soapinterop.org/", "ObtenerInfoCertificado"));
		if ((usuari != null) && (!usuari.equals(""))) {
			try {
				call.setClientHandlers(new AfirmaSecurityHandler(usuari, password), null);
			} catch (Exception ex) {
				throw new ValidacioFirmaException("(Call) Error en la cridada a aFirma", ex);
			}
		}
		call.setReturnType(org.apache.axis.Constants.XSD_STRING);
		call.addParameter("ObtenerInfoCertificadoRequest", org.apache.axis.Constants.XSD_STRING, javax.xml.rpc.ParameterMode.IN);
		String xmlResposta = (String)call.invoke(new Object[]{xmlPeticio});
		logSiActivat(xmlResposta);
		return xmlResposta;
	}
	private String cridarValidarFirma(String signaturaBase64, String documentBase64) throws Exception {
		
		String xmlPeticio = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<mensajeEntrada xmlns=\"http://afirmaws/ws/firma\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/ws.xsd\">" +
			"<peticion>ValidarFirma</peticion>" +
			"<versionMsg>1.0</versionMsg>" +
			"<parametros>" +
			"<idAplicacion>" + aplicacioId + "</idAplicacion>" +
			"<firmaElectronica><![CDATA[" + signaturaBase64 + "]]></firmaElectronica>" +
			((formatSignatura != null) ? "<formatoFirma>" + formatSignatura + "</formatoFirma>" : "") +
			"<datos><![CDATA[" + documentBase64 + "]]></datos>" +
			"</parametros>" +
			"</mensajeEntrada>";
		logSiActivat(xmlPeticio);
		Service service = new Service();
		Call call = (Call)service.createCall();
		call.setTargetEndpointAddress(baseUrl + "/ValidarFirma");
		call.setOperationName(new QName("http://soapinterop.org/", "ValidarFirma"));
		if ((usuari != null) && (!usuari.equals(""))) {
			try {
				call.setClientHandlers(new AfirmaSecurityHandler(usuari, password), null);
			} catch (Exception ex) {
				throw new ValidacioFirmaException("(Call) Error en la cridada a aFirma", ex);
			}
		}
		call.setReturnType(org.apache.axis.Constants.XSD_STRING);
		call.addParameter("ValidarFirmaRequest", org.apache.axis.Constants.XSD_STRING, javax.xml.rpc.ParameterMode.IN);
		String xmlResposta = (String)call.invoke(new Object[]{xmlPeticio});
		logSiActivat(xmlResposta);
		return xmlResposta;
	}

	private void logSiActivat(String missatge) {
		
		if (logMissatges) {
			log.info("-------------------------------------");
			log.info(missatge);
			log.info("-------------------------------------");
		}
	}

}
