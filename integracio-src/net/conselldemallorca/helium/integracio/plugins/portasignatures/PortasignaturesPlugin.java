package net.conselldemallorca.helium.integracio.plugins.portasignatures;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import javax.activation.DataHandler;
import javax.activation.DataSource;

import net.conselldemallorca.helium.integracio.plugins.persones.Persona;
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
import net.conselldemallorca.helium.model.dto.DocumentDto;
import net.conselldemallorca.helium.model.hibernate.Expedient;
import net.conselldemallorca.helium.util.ConvertirPDF;
import net.conselldemallorca.helium.util.GlobalProperties;

import org.apache.axis.attachments.AttachmentPart;

/**
 * Implementació del plugin de portasignatures.
 * 
 * @author Miquel Angel Amengual <miquelaa@limit.es>
 */
public class PortasignaturesPlugin {
	
	private String user = GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.usuari");
	private String password = GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.password");

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
	public Integer UploadDocument (
			Persona persona,
			DocumentDto documentDto,
			Expedient expedient,
			String importancia,
			Date dataLimit) throws Exception {
		UploadRequest request = new UploadRequest();
		
		// Aplicació.
		Application application = new Application();
		application.setUser(user);
		application.setPassword(password);
		request.setApplication(application);
		
		// Document.
		UploadRequestDocument document = new UploadRequestDocument();

		// Atributs del document.
		DocumentAttributes attributes = new DocumentAttributes();
		
		/*
		 * Atributs requerits.
		 */
		String nomArxiu = documentDto.getArxiuNom().substring(0, documentDto.getArxiuNom().lastIndexOf("."));
		attributes.setTitle(nomArxiu);
		
		String extensioArxiu = documentDto.getArxiuNom().substring(documentDto.getArxiuNom().lastIndexOf(".") + 1);
		String extensio = extensioArxiu;
		if (!extensio.equals("pdf")) {
			extensio = "pdf";
		}
		attributes.setExtension(extensio);
		
		/*
		 * Atributs no requerits.
		 */
		// Tipus de document al que pertany.
		if (documentDto.getTipusDocPortasignatures() != null) {
			attributes.setType(documentDto.getTipusDocPortasignatures());
		}
		
		attributes.setSubject(nomArxiu);
		attributes.setDescription(nomArxiu);
		
		// Informació adicional del responsable del document.
		Sender sender = new Sender();
		sender.setName(expedient.getTitol());
		attributes.setSender(sender);
		
		// Especificamos la importancia del document.
		attributes.setImportance(ImportanceEnum.fromString(importancia));
		
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
		
		attributes.setTypeSign(1);		// 1 - PDF; 2 - P7/CMS/CADES; 3 - XADES;
		
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
		
		Boolean checkCert = "true".equals((String)GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.checkcerts"));
		
		// És necessari que el DNI del certificat coincideixi amb el DNI de l'usuari logat a Portafirmas.
		Signer signer = new Signer();
		signer.setId(persona.getDni());
		signer.setCheckCert(checkCert);
		
		// Afegim el firmant a l'etapa.
		step.setSigners(new Signer[]{ signer });
		
		steps.setStep(new Step[]{ step });
		
		// Afegim les etapes al document.
		document.setSteps(steps);
		
		// Guardam el document al request.
		request.setDocument(document);
		
		// Cream la connexió.
		CwsProxy factory = new CwsProxy();
		factory.setEndpoint((String)GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.url"));
		CWSSoapBindingStub stub = (CWSSoapBindingStub)factory.getCws();
		
		// Classe per convertir documents.
		ConvertirPDF convertir = new ConvertirPDF();

		final String contentType = ((documentDto.getContentType() == null) || ((documentDto.getContentType()).equals("")))?convertir.getArxiuMimeType(documentDto.getArxiuNom()):documentDto.getContentType();
		final String name = documentDto.getArxiuNom();
		final byte[] contingut = documentDto.getArxiuContingut();
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataSource dataSource = new DataSource() {
			public OutputStream getOutputStream() throws IOException { return baos; }
			public InputStream getInputStream() throws IOException { return new ByteArrayInputStream(contingut); }
			public String getName() { return name; }
			public String getContentType() { return contentType; }
		};
		
		// Cream el fitxer a adjuntar.
		if (!extensioArxiu.equals("pdf")) {
			byte[] convertit = convertir.render(documentDto.getArxiuContingut(), documentDto.getArxiuNom());
			
			if (convertit != null) {
				try {
					final String contentTypeAttachment = "application/pdf";
					final String nameAttachment = nomArxiu + ".pdf";
					final byte[] contingutAttachment = convertit;
					final ByteArrayOutputStream baosAttachment = new ByteArrayOutputStream();
					DataSource dataSourceAttachment = new DataSource() {
						public OutputStream getOutputStream() throws IOException { return baosAttachment; }
						public InputStream getInputStream() throws IOException { return new ByteArrayInputStream(contingutAttachment); }
						public String getName() { return nameAttachment; }
						public String getContentType() { return contentTypeAttachment; }
					};
					
					DataHandler attachmentFile = new DataHandler(dataSourceAttachment);
					stub.addAttachment(attachmentFile);
				} catch (Exception e) {
					throw new Exception("Error en la conversio del document", e);
				}
			}
		} else {
			DataHandler attachmentFile = new DataHandler(dataSource);
			stub.addAttachment(attachmentFile);
		}
		
		// Llançam la petició i obtenim la resposta.
		UploadResponse response = stub.uploadDocument(request);
		
		return response.getDocument().getId();
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
	@SuppressWarnings({"unchecked", "unused"})
	public byte[] DownloadDocument(
			Integer documentId) throws Exception {
		
		DownloadRequest request = new DownloadRequest();
		DownloadRequestDocument requestDocument = new DownloadRequestDocument();
		
		requestDocument.setId(documentId);
		
		// Aplicació.
		Application application = new Application();
		application.setUser(user);
		application.setPassword(password);
		request.setApplication(application);
				
		request.setDocument(requestDocument);
		request.setDownloadDocuments(Boolean.TRUE);
		request.setAdditionalInfo(Boolean.FALSE);
		request.setArchiveInfo(Boolean.FALSE);
		
		// Cream la connexió.
		CwsProxy factory = new CwsProxy();
		factory.setEndpoint((String)GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.url"));
		CWSSoapBindingStub stub = (CWSSoapBindingStub)factory.getCws();
		
		// Llançam la petició i obtenim la resposta.
		DownloadResponse response = stub.downloadDocument(request);
		
		Iterator attachs = stub._getCall().getMessageContext().getCurrentMessage().getAttachments();
		AttachmentPart attach = (AttachmentPart)attachs.next();
		DataHandler dataHandler = attach.getActivationDataHandler();
		if (dataHandler == null) {
			throw new Exception("Error obtenint els adjunts de la resposta del Portasignatures");
		}
		attach.detachAttachmentFile();
		
		try {
			byte[] resposta = new byte[dataHandler.getInputStream().available()];
			dataHandler.getInputStream().read(resposta);
			
			if ((resposta == null) || resposta.equals("")) {
				throw new Exception("El contingut del document es buit.");
			}
			
			return resposta;
		} catch (Exception e) {
			throw new Exception("Error obtenint el contingut del document.", e);
		}
	}
}
