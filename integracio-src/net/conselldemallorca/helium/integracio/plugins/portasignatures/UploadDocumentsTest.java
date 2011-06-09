/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.portasignatures;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.integracio.plugins.portasignatures.service.wsdl.Application;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.service.wsdl.Attributes;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.service.wsdl.CallbackRequest;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.service.wsdl.CallbackResponse;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.service.wsdl.Document;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.service.wsdl.MCGDws;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.service.wsdl.MCGDwsService;
import net.conselldemallorca.helium.util.GlobalProperties;

import org.springframework.core.io.FileSystemResource;

/**
 * Client de WebService per enviar un document al portasignatures
 * 
 * @author Miquel Angel Amengual <miquelaa@limit.es>
 */
public class UploadDocumentsTest {

	public static void main(String[] args) throws Exception {
		try {
			new GlobalProperties(new FileSystemResource("c:/tmp/helium/global.properties"));
			UploadDocumentsTest test = new UploadDocumentsTest();
			test.upload();
			//test.download();
			//test.callback();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void upload() throws Exception {
		PortasignaturesPluginCaib plugin = new PortasignaturesPluginCaib();
		String arxiuNom = "original.pdf";
		Date dataLimit = new Date();
		dataLimit.setTime(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
		byte[] arxiuContingut = getResourceContent(arxiuNom);
		Integer documentId = plugin.uploadDocument(
				"sistraoper",
				"Document de prova d'integració",
				arxiuNom,
				arxiuContingut,
				999,
				"Gestor d'expedients administratius Helium",
				null,
				dataLimit);
		System.out.println(">>> documentId: " + documentId);
	}
	public void download() throws Exception {
		PortasignaturesPluginCaib plugin = new PortasignaturesPluginCaib();
		List<byte[]> signatures = plugin.obtenirSignaturesDocument(new Integer(32));
		System.out.println(">>> núm. signatures: " + signatures.size());
	}
	public void callback() throws Exception {
		MCGDws service = new MCGDwsService(new URL("http://siroco2.lanbd.conselldemallorca.net:8080/helium/ws/MCGDws?wsdl")).getMCGDWS();
		CallbackRequest request = new CallbackRequest();
		Application application = new Application();
		Document document = new Document();
		document.setId(32);
		Attributes attributes = new Attributes();
		attributes.setState(2);
		document.setAttributes(attributes);
		application.setDocument(document);
		request.setApplication(application);
		CallbackResponse response = service.callback(request);
		System.out.println(">>> return: " + response.getReturn());
	}



	private byte[] getResourceContent(String resourceName) throws Exception {
		InputStream is = getClass().getResourceAsStream(resourceName);
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[16384];
		while ((nRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}
		buffer.flush();
		return buffer.toByteArray();
	}

}
