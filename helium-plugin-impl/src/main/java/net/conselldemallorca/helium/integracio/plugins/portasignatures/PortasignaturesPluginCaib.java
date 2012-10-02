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
import net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl.Annex;
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
 * @author Limit Tecnologies <limit@limit.es>
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
			DocumentPortasignatures document,
			List<DocumentPortasignatures> annexos,
			boolean isSignarAnnexos,
			PasSignatura[] passesSignatura,
			String remitent,
			String importancia,
			Date dataLimit) throws PortasignaturesPluginException {
		CwsProxy factory = new CwsProxy();
		factory.setEndpoint((String)GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.url"));
		CWSSoapBindingStub stub = (CWSSoapBindingStub)factory.getCws();
		try {
			// Afegeix el document a signar (sempre és el primer document que s'adjunta)
			afegirAttachmentSoap(
					stub,
					document.getArxiuNom(),
					document.getArxiuContingut());
			// Afegeix els annexos
			if (annexos != null) {
				for (DocumentPortasignatures annex: annexos) {
					afegirAttachmentSoap(
							stub,
							annex.getArxiuNom(),
							annex.getArxiuContingut());
				}
			}
			UploadRequest request = new UploadRequest(
					getRequestApplication(),
					getRequestDocument(
							document,
							annexos,
							isSignarAnnexos,
							passesSignatura,
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
	@SuppressWarnings("rawtypes")
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
				Iterator attachs = stub._getCall().getMessageContext().getCurrentMessage().getAttachments();
				if (attachs.hasNext()) {
					AttachmentPart attach = (AttachmentPart)attachs.next();
					DataHandler dataHandler = attach.getActivationDataHandler();
					if (dataHandler != null) {
						//attach.detachAttachmentFile();
						byte[] bytes = new byte[dataHandler.getInputStream().available()];
						dataHandler.getInputStream().read(bytes);
						if (bytes.length == 0) {
							throw new PortasignaturesPluginException("El contingut de la signatura es buit");
						}
						resposta.add(bytes);
					} else {
						throw new PortasignaturesPluginException("El contingut de la signatura es null");
					}
					attach.dispose();
				} else {
					throw new PortasignaturesPluginException("La resposta no ha retornat cap signatura");
				}
				return resposta;
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

	private void afegirAttachmentSoap(
			CWSSoapBindingStub stub,
			String arxiuNom,
			byte[] arxiuContingut) throws Exception {
		DataSource ds = new ByteArrayDataSource(
				arxiuContingut,
				arxiuNom,
				getOpenOfficeUtils().getArxiuMimeType(arxiuNom));
		DataHandler attachmentFile = new DataHandler(ds);
		stub.addAttachment(attachmentFile);
	}

	private Application getRequestApplication() {
		Application application = new Application();
		application.setUser(getUserName());
		application.setPassword(getPassword());
		return application;
	}

	private UploadRequestDocument getRequestDocument(
			DocumentPortasignatures document,
			List<DocumentPortasignatures> annexos,
			boolean isSignarAnnexos,
			PasSignatura[] passesSignatura,
			String remitent,
			String importancia,
			Date dataLimit) {
		// Document.
		UploadRequestDocument documentRequest = new UploadRequestDocument();
		DocumentAttributes attributes = new DocumentAttributes();
		// Atributs obligatoris
		attributes.setTitle(limitarString(document.getTitol(), 100));
		attributes.setExtension(getDocumentArxiuExtensio(document.getArxiuNom()));
		// Atributs opcionals
		attributes.setDescription(document.getTitol());
		//attributes.setSubject(arxiuDescripcio);
		if (document.getTipus() != null)
			attributes.setType(document.getTipus());
		// Informació adicional del responsable del document
		Sender sender = new Sender();
		sender.setName(limitarString(remitent, 50));
		attributes.setSender(sender);
		// Importancia del document
		if (importancia != null)
			attributes.setImportance(ImportanceEnum.fromString(importancia));
		else
			attributes.setImportance(ImportanceEnum.normal);
		if (dataLimit != null) {
			Calendar cal = Calendar.getInstance();
			// Data d'enviament del document
			attributes.setDateNotice(cal);
			// Data límit per signar el document
			cal.setTime(dataLimit);
			attributes.setDateLimit(cal);
		}
		// 1 - PDF; 2 - P7/CMS/CADES; 3 - XADES;
		attributes.setTypeSign(getSignaturaTipus());
		// Indica si el document ja està signat i es volen afegir més signatures.
		attributes.setIsFileSign(document.isSignat());
		// Afegim els atributs al document.
		documentRequest.setAttributes(attributes);
		// Afegim els annexos
		if (annexos != null) {
			List<Annex> anxs = new ArrayList<Annex>();
			for (DocumentPortasignatures annex: annexos) {
				Annex anx = new Annex();
				anx.setDescription(limitarString(annex.getTitol(), 250));
				anx.setExtension(getDocumentArxiuExtensio(annex.getArxiuNom()));
				anx.setReference(annex.getReference());
				anxs.add(anx);
				Sender anxSender = new Sender();
				anxSender.setName(remitent);
				anx.setSender(anxSender);
			}
			documentRequest.setAnnexes(
					anxs.toArray(new Annex[anxs.size()]));
			attributes.setNumberAnnexes(anxs.size());
		} else {
			attributes.setNumberAnnexes(0);
		}
		attributes.setSignAnnexes(isSignarAnnexos);
		// Cream les etapes i especificam el tipus de firma.
		Steps steps = new Steps();
		steps.setSignMode(SignModeEnum.attached);
		List<Step> stepList = new ArrayList<Step>();
		for (PasSignatura pas: passesSignatura) {
			// Cream una etapa amb un firmant.
			UploadStep step = new UploadStep();
			step.setMinimalSigners(pas.getMinSignataris());
			List<Signer> signerList = new ArrayList<Signer>();
			for (String signatari: pas.getSignataris()) {
				// És necessari que el DNI del certificat coincideixi amb el DNI de l'usuari logat a Portafirmas.
				Signer signer = new Signer();
				signer.setId(signatari);
				signer.setCheckCert(new Boolean(isCheckCert()));
				signerList.add(signer);
			}
			step.setSigners(
					signerList.toArray(
							new Signer[pas.getSignataris().length]));
			stepList.add(step);
		}
		steps.setStep(
				stepList.toArray(
						new Step[passesSignatura.length]));
		// Afegim les etapes al document.
		documentRequest.setSteps(steps);
		return documentRequest;
	}

	private String getDocumentArxiuExtensio(String arxiuNom) {
		int index = arxiuNom.lastIndexOf(".");
		if (index != -1) {
			return arxiuNom.substring(index + 1);
		} else {
			return "";
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

	private String limitarString(String str, int maxChars) {
		String textFinal = "[...]";
		if (str.length() > maxChars) {
			return str.substring(0, maxChars - textFinal.length()) + textFinal;
		} else {
			return str;
		}
	}

}
