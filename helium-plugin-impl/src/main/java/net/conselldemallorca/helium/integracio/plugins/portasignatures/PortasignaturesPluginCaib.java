package net.conselldemallorca.helium.integracio.plugins.portasignatures;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;

import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.core.util.OpenOfficeUtils;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl.Application;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl.CWSSoapBindingStub;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl.CwsProxy;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl.DocumentAttributes;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl.DownloadRequest;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl.DownloadRequestDocument;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl.DownloadResponse;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl.ImportanceEnum;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl.Sender;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl.SignModeEnum;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl.Signer;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl.Step;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl.Steps;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl.UploadRequest;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl.UploadRequestDocument;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl.UploadResponse;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl.UploadStep;

import org.apache.axis.attachments.AttachmentPart;

/**
 * Implementació del plugin de portasignatures per la CAIB.
 * 
 * @author Miquel Angel Amengual <miquelaa@limit.es>
 */
public class PortasignaturesPluginCaib implements PortasignaturesPlugin {

	private OpenOfficeUtils openOfficeUtils;



	/**
	 * Puja un document al Portasignatures.
	 * 
	 * @param persona
	 * @param documentDto
	 * @param expedient
	 * @param importancia
	 * @param dataLimit
	 * 
	 * @return Id del document al Portasignatures
	 * 
	 * @throws Exception
	 */
	public Integer uploadDocument (
			String signatariId,
			String arxiuDescripcio,
			String arxiuNom,
			byte[] arxiuContingut,
			Integer tipusDocument,
			String remitent,
			String importancia,
			Date dataLimit) throws PortasignaturesPluginException {

		// Cream la connexió.
		CwsProxy factory = new CwsProxy();
		factory.setEndpoint((String)GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.url"));
		CWSSoapBindingStub stub = (CWSSoapBindingStub)factory.getCws();
		
		// Enviam el document a convertir.
		try {
			DataHandler attachmentFile = new DataHandler(
					getDocumentDataSource(
							arxiuNom,
							arxiuContingut));
			stub.addAttachment(attachmentFile);
			UploadRequest request = new UploadRequest(
					getRequestApplication(),
					getRequestDocument(
							arxiuNom,
							arxiuDescripcio,
							signatariId,
							tipusDocument,
							remitent,
							importancia,
							dataLimit),
					null);
			UploadResponse response = stub.uploadDocument(request);
			if (response.getResult().getCode() == 0) {
				if (response.getDocument() != null) {
					return response.getDocument().getId();
				} else {
					throw new PortasignaturesPluginException("Error al enviar el document al portasignatures: la resposta no ha retornat cap document");
				}
			} else {
				throw new PortasignaturesPluginException("Error al enviar el document al portasignatures: " + response.getResult().getMessage());
			}
		} catch (Exception ex) {
			throw new PortasignaturesPluginException("Error al enviar el document al portasignatures", ex);
		}
	}

	/**
	 * Descarrega un document del Portasignatures
	 * 
	 * @param documentId
	 * 
	 * @return document
	 * 
	 * @throws Exception
	 */
	public List<byte[]> obtenirSignaturesDocument(
			Integer documentId) throws PortasignaturesPluginException {
		
		DownloadRequest request = new DownloadRequest();
		DownloadRequestDocument requestDocument = new DownloadRequestDocument();
		
		requestDocument.setId(documentId);
		
		// Aplicació.
		Application application = new Application();
		application.setUser(getUserName());
		application.setPassword(getPassword());
		request.setApplication(application);
				
		request.setDocument(requestDocument);
		request.setDownloadDocuments(Boolean.TRUE);
		request.setAdditionalInfo(Boolean.TRUE);
		request.setArchiveInfo(Boolean.TRUE);
		
		// Cream la connexió.
		CwsProxy factory = new CwsProxy();
		factory.setEndpoint((String)GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.url"));
		CWSSoapBindingStub stub = (CWSSoapBindingStub)factory.getCws();
		try {
			// Llançam la petició i obtenim la resposta.
			DownloadResponse response = stub.downloadDocument(request);
			if (response.getResult().getCode() == 0) {
				List<byte[]> resposta = new ArrayList<byte[]>();
				@SuppressWarnings("rawtypes")
				Iterator attachs = stub._getCall().getMessageContext().getCurrentMessage().getAttachments();
				if (attachs.hasNext()) {
					AttachmentPart attach = (AttachmentPart)attachs.next();
					DataHandler dataHandler = attach.getActivationDataHandler();
					if (dataHandler != null) {
						attach.detachAttachmentFile();
						byte[] bytes = new byte[dataHandler.getInputStream().available()];
						dataHandler.getInputStream().read(bytes);
						if (bytes.length == 0) {
							throw new PortasignaturesPluginException("El contingut de la signatura es buit");
						}
						resposta.add(bytes);
						return resposta;
					} else {
						throw new PortasignaturesPluginException("El contingut de la signatura es null");
					}
				} else {
					throw new PortasignaturesPluginException("La resposta no ha retornat cap signatura");
				}
			} else {
				throw new PortasignaturesPluginException("Error obtenint les signatures del portasignatures: " + response.getResult().getMessage());
			}
		} catch (IOException ex) {
			throw new PortasignaturesPluginException("Error obtenint les signatures del portasignatures", ex);
		}
	}



	private boolean isCheckCert() {
		return "true".equals(GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.checkcerts"));
	}
	private boolean isConvertirDocument() {
		return "true".equals(GlobalProperties.getInstance().getProperty("app.conversio.portasignatures.actiu"));
	}
	private String getConvertirExtensio() {
		return GlobalProperties.getInstance().getProperty("app.conversio.portasignatures.extension");
	}
	private String getUserName() {
		return GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.usuari");
	}
	private String getPassword() {
		return GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.password");
	}
	private Integer getSignaturaTipus() {
		String tipus = GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.signatura.tipus");
		if (tipus != null)
			return new Integer(tipus);
		else
			return new Integer(1);
	}

	private DataSource getDocumentDataSource(
			String arxiuNom,
			byte[] arxiuContingut) throws Exception {
		if (isConvertirDocument()) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			getOpenOfficeUtils().convertir(
					arxiuNom,
					arxiuContingut,
					getConvertirExtensio(),
					baos);
			return new ByteArrayDataSource(
					baos.toByteArray(),
					getOpenOfficeUtils().nomArxiuConvertit(arxiuNom, getConvertirExtensio()),
					getOpenOfficeUtils().getArxiuMimeType(arxiuNom));
		} else {
			return new ByteArrayDataSource(
					arxiuContingut,
					arxiuNom,
					getOpenOfficeUtils().getArxiuMimeType(arxiuNom));
		}
	}

	private Application getRequestApplication() {
		Application application = new Application();
		application.setUser(getUserName());
		application.setPassword(getPassword());
		return application;
	}

	private UploadRequestDocument getRequestDocument(
			String arxiuNom,
			String arxiuDescripcio,
			String signatariId,
			Integer tipusDocument,
			String remitent,
			String importancia,
			Date dataLimit) {
		// Document.
		UploadRequestDocument document = new UploadRequestDocument();

		// Atributs del document.
		DocumentAttributes attributes = new DocumentAttributes();
		
		/*
		 * Atributs requerits.
		 */
		attributes.setTitle(arxiuDescripcio);
		attributes.setExtension(getDocumentArxiuExtensioFinal(arxiuNom));
		
		/*
		 * Atributs no requerits.
		 */
		// Tipus de document al que pertany.
		if (tipusDocument != null) {
			attributes.setType(tipusDocument);
		}
		
		//attributes.setSubject(arxiuDescripcio);
		//attributes.setDescription(arxiuDescripcio);
		
		// Informació adicional del responsable del document.
		Sender sender = new Sender();
		sender.setName(remitent);
		attributes.setSender(sender);
		
		// Especificamos la importancia del document.
		if (importancia != null)
			attributes.setImportance(ImportanceEnum.fromString(importancia));
		else
			attributes.setImportance(ImportanceEnum.normal);
		
		if (dataLimit != null) {
			Calendar cal = Calendar.getInstance();
			// Data d'enviament del document.
			attributes.setDateNotice(cal);
		
			// Data límit de firma del document.
			cal.setTime(dataLimit);
			attributes.setDateLimit(cal);
		}
		
		attributes.setNumberAnnexes(0);
		attributes.setSignAnnexes(false);
		
		attributes.setTypeSign(getSignaturaTipus());		// 1 - PDF; 2 - P7/CMS/CADES; 3 - XADES;
		
		// Indica si el document a signar ja és un document de signatura i es volen afegir més signatures.
		attributes.setIsFileSign(false);
		
		// Afegim els atributs al document.
		document.setAttributes(attributes);
		
		// Cream les etapes i especificam el tipus de firma.
		Steps steps = new Steps();
		steps.setSignMode(SignModeEnum.attached);
		
		// Cream una etapa amb un firmant.
		UploadStep step = new UploadStep();
		step.setMinimalSigners(1);
		
		// És necessari que el DNI del certificat coincideixi amb el DNI de l'usuari logat a Portafirmas.
		Signer signer = new Signer();
		signer.setId(signatariId);
		signer.setCheckCert(new Boolean(isCheckCert()));
		
		// Afegim el firmant a l'etapa.
		step.setSigners(new Signer[]{ signer });
		
		steps.setStep(new Step[]{ step });
		
		// Afegim les etapes al document.
		document.setSteps(steps);
		return document;
	}

	private String getDocumentArxiuExtensioFinal(
			String arxiuNom) {
		if (isConvertirDocument() && getConvertirExtensio() != null) {
			return getConvertirExtensio();
		} else {
			int index = arxiuNom.lastIndexOf(".");
			if (index != -1) {
				return arxiuNom.substring(index + 1);
			} else {
				return null;
			}
		}
	}

	private OpenOfficeUtils getOpenOfficeUtils() {
		if (openOfficeUtils == null) {
			openOfficeUtils = new OpenOfficeUtils();
		}
		return openOfficeUtils;
	}

	private class ByteArrayDataSource implements DataSource {
		private byte[] dades;
		private String name;
		private String contentType;
		public ByteArrayDataSource(byte[] dades, String name, String contentType) {
			this.dades = dades;
			this.name = name;
			this.contentType = contentType;
		}
		public OutputStream getOutputStream() throws IOException {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(dades);
			return baos;
		}
		public InputStream getInputStream() throws IOException {
			return new ByteArrayInputStream(dades);
		}
		public String getName() {
			return name;
		}
		public String getContentType() {
			return contentType;
		}
	}

}
