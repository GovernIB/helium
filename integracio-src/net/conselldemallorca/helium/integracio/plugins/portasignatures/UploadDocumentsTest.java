/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.portasignatures;

import java.util.Calendar;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl.Application;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl.CWSSoapBindingStub;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl.CwsProxy;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.wsdl.DocumentAttributes;
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

/**
 * Client de WebService per enviar un document al portasignatures
 * 
 * @author Miquel Angel Amengual <miquelaa@limit.es>
 */
public class UploadDocumentsTest {

	public static void main(String[] args) throws Exception {
		UploadDocumentsTest instance = new UploadDocumentsTest();
		CwsProxy factory = new CwsProxy();
		factory.setEndpoint("https://proves.caib.es/portafirmasws/web/services/CWS");
		
		DataHandler attachmentFile = new DataHandler(new FileDataSource("C:\\testLimit.pdf"));
		CWSSoapBindingStub stub = (CWSSoapBindingStub)factory.getCws();
		stub.addAttachment(attachmentFile);
		
		UploadRequest request = instance.buildRequest();
		UploadResponse response = stub.uploadDocument(request);
		System.out.println("Versió: " + response.getVersion());
		System.out.println("Resultat Codi: " + response.getResult().getCode());
		System.out.println("Resultat Missatge: " + response.getResult().getMessage());
		System.out.println("Id del document: " + response.getDocument().getId());
	}

	public UploadRequest buildRequest() {
		UploadRequest request = new UploadRequest();
		
		// Aplicació.
		Application application = new Application();
		application.setUser("HELIUM");
		application.setPassword("HELIUM");
		request.setApplication(application);

		// Document
		UploadRequestDocument document = new UploadRequestDocument();

		DocumentAttributes attributes = new DocumentAttributes();
		
		/*
		 * Elements requerits.
		 */
		attributes.setTitle("TEST Limit");
		attributes.setExtension("pdf");
		
		/*
		 * Elementos no requerits.
		 */
		// Tipus de document al que pertany.
		attributes.setType(Integer.valueOf("1"));		// Altres.
		
		attributes.setSubject("Test Limit");
		attributes.setDescription("TEST Limit");
		attributes.setUrl("");
		
		// Informació adicional del responsable del document.
		Sender sender = new Sender();
		sender.setName("proves");
		attributes.setSender(sender);
		
		// Especificamos la importancia del document.
		attributes.setImportance(ImportanceEnum.high);
		
		// Data límit de firma del document.
		Calendar cal = Calendar.getInstance();
		cal.set(2010, 10, 10);
		attributes.setDateLimit(cal);
		
		cal.set(2010, 4, 10);
		attributes.setDateNotice(cal);
		
		attributes.setNumberAnnexes(0);
		attributes.setSignAnnexes(false);
		attributes.setType(1);
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
		signer.setId("12345678Z");
		signer.setCheckCert(false);
		
		// Afegim el firmant a l'etapa.
		step.setSigners(new Signer[]{ signer });
		
		steps.setStep(new Step[]{ step });
		
		// Afegim les etapes al document.
		document.setSteps(steps);
		
		// Guardam el document al request.
		request.setDocument(document);

		return request;
	}

}
