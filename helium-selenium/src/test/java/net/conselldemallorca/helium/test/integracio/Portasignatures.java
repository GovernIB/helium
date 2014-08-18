package net.conselldemallorca.helium.test.integracio;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import net.conselldemallorca.helium.integracio.plugins.portasignatures.PortasignaturesPlugin;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.PortasignaturesPluginException;
import net.conselldemallorca.helium.test.integracio.utils.PortasignaturesCallback;
import net.conselldemallorca.helium.test.util.BaseTest;
import net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Application;
import net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.CWSSoapBindingStub;
import net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.CwsService;
import net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.CwsServiceLocator;
import net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DeleteRequest;
import net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DeleteRequestDocument;
import net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DeleteResponse;
import net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DocumentAttributes;
import net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadRequest;
import net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadRequestDocument;
import net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DownloadResponse;
import net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ImportanceEnum;
import net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListTypeRequest;
import net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ListTypeResponse;
import net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Sender;
import net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SignModeEnum;
import net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Signer;
import net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Steps;
import net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UploadRequest;
import net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UploadRequestDocument;
import net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UploadResponse;
import net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.UploadStep;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.sun.xml.internal.ws.util.ByteArrayDataSource;

@SuppressWarnings("restriction")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Portasignatures extends BaseTest {
	private CWSSoapBindingStub stub;

	@BeforeClass
	public static void beforeClass() {
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.Log4JLogger");
		System.setProperty("org.apache.commons.logging.LogFactory", "org.apache.commons.logging.impl.LogFactoryImpl");
	}

	@Test
	public void a_getPortasignaturesPlugin() {
		PortasignaturesPlugin portasignaturesPlugin = null;
		String pluginClass = properties.getProperty("app.portasignatures.plugin.class");
		if ((pluginClass != null) && (pluginClass.length() > 0)) {
			try {
				Class<?> clazz = Class.forName(pluginClass);
				portasignaturesPlugin = (PortasignaturesPlugin) clazz.newInstance();
			} catch (Exception ex) {
				portasignaturesPlugin = null;
			}
		}

		assertNotNull("No s'ha pogut crear la instància del plugin de portasignatures", portasignaturesPlugin);
	}

	@Test
	public void b_stub() {
		CwsService service;

		try {
			service = new CwsServiceLocator();

			String urlEndPoint = properties.getProperty("app.portasignatures.plugin.url");
			stub = (CWSSoapBindingStub) service.getCWS(new URL(urlEndPoint));
		} catch (Exception ex) {
			service = null;
			stub = null;
		}

		assertTrue("Error cliente WS ", service != null && stub != null);
	}

	@Test
	// Recupera la lista de tipos
	public void c_listType() throws PortasignaturesPluginException {		
		boolean resultado = false;
		ListTypeRequest listTypeRequest = new ListTypeRequest();
		listTypeRequest.setApplication(getRequestApplication());

		ListTypeResponse response = null;
		try {
			b_stub();
			response = stub.listTypeDocuments(listTypeRequest);

			System.out.println("Version: " + response.getVersion());
			System.out.println("Resultado Codigo: " + response.getResult().getCode());
			System.out.println("Resultado Mensaje: " + response.getResult().getMessage());

			resultado = response.getResult().getCode() == 0;

			assertTrue("Error al recuperar la lista de tipos de documentos del portasignatures: " + response.getResult().getMessage(), resultado);
		} catch (Exception e) {
			assertTrue("Error al recuperar la lista de tipos de documentos del portasignatures: se produjo un error en la llamada.", resultado);
		}

		assertTrue("Error al recuperar la lista de tipos de documentos del portasignatures: la resposta no ha retornat cap document.", resultado);
	}

	@Test
	// Puja un document al Portasignatures.
	public void d_uploadDocument() throws PortasignaturesPluginException {
		boolean resultado = false;

		try {
			UploadRequest request = new UploadRequest();
			request.setDocument(getRequestDocument());
			b_stub();
			stub.setTimeout(100000);

			// Test version
			request.setApplication(getRequestApplication());

			UploadResponse response = stub.uploadDocument(request);

			 System.out.println("Version: " + response.getVersion());
			 System.out.println("Resultado Codigo: " + response.getResult().getCode());
			 System.out.println("Resultado Mensaje: " + response.getResult().getMessage());

			resultado = response.getDocument() != null && response.getResult().getCode() == 0;
			assertTrue("Error al enviar el document al portasignatures: " + response.getResult().getMessage(), resultado);
		} catch (Exception ex) {
			assertTrue("Error al enviar el document al portasignatures: " + ex.getMessage(), resultado);
		}
		assertTrue("Error al enviar el document al portasignatures: la resposta no ha retornat cap document", resultado);
	}

	 @Test
	// Descarrega un document del Portasignatures
	public void e_obtenirSignaturesDocument() throws PortasignaturesPluginException {
		// Bajamos el documento
		 
		DownloadRequest request = new DownloadRequest();
		request.setVersion("1");
		DownloadRequestDocument requestDocument = new DownloadRequestDocument();
		requestDocument.setId(1);
		List<byte[]> resposta = null;

		// Aplicació.
		request.setApplication(getRequestApplication());

		request.setDownloadDocuments(Boolean.TRUE);
		request.setAdditionalInfo(Boolean.TRUE);
		request.setArchiveInfo(Boolean.TRUE);

		request.setDocument(requestDocument);

		try {
			b_stub();
			
			// Llançam la petició i obtenim la resposta.
			DownloadResponse response = stub.downloadDocument(request);
			if (response.getResult().getCode() == 0) {
				resposta = new ArrayList<byte[]>();
				afegirAttachmentSoap();
				Object[] attachs = stub.getAttachments();
				if (attachs.length>0) {
					for (Object attach : attachs) {
						DataHandler dataHandler = (DataHandler) attach;
						// attach.detachAttachmentFile();
						byte[] bytes = new byte[dataHandler.getInputStream().available()];
						dataHandler.getInputStream().read(bytes);
						assertTrue("El contingut de la signatura es buit", bytes.length != 0);
						resposta.add(bytes);
					}
				} else {
					assertNotNull("La resposta no ha retornat cap signatura", resposta);
				}
			} else {
				assertNotNull("Error obtenint les signatures del portasignatures: " + response.getResult().getMessage(), resposta);
			}
		} catch (IOException ex) {
			assertNotNull("Error obtenint les signatures del portasignatures", resposta);
		}
	}

	@Test
	public void f_portasignatureCallback() {
		boolean resultado = false;
		try {
			PortasignaturesCallback portasignatureCallback = new PortasignaturesCallback();
			double res = 0;

			// DOCUMENT_SIGNAT
			res = portasignatureCallback.testDocumentPortasignatures(383147230, PortasignaturesCallback.DOCUMENT_SIGNAT);
			resultado = res == new Double(-1.0);
			assertTrue("Error al obtener la respuesta del portasignatures: PortasignaturesCallback.DOCUMENT_SIGNAT ", resultado);

			// DOCUMENT_BLOQUEJAT
			res = portasignatureCallback.testDocumentPortasignatures(119418, PortasignaturesCallback.DOCUMENT_BLOQUEJAT);
			// INFO: Fi procés petició callback portasignatures (id=383147230, estat=0-Bloquejat, resposta=1.0)
			resultado = res == new Double(1.0);
			assertTrue("Error al obtener la respuesta del portasignatures: PortasignaturesCallback.DOCUMENT_BLOQUEJAT ", resultado);

			// DOCUMENT_PENDENT
			res = portasignatureCallback.testDocumentPortasignatures(383147230, PortasignaturesCallback.DOCUMENT_PENDENT);
			// INFO: Fi procés petició callback portasignatures (id=119418, estat=0-Bloquejat, resposta=1.0)
			resultado = res == new Double(1.0);
			assertTrue("Error al obtener la respuesta del portasignatures: PortasignaturesCallback.DOCUMENT_PENDENT ", resultado);

			// DOCUMENT_REBUTJAT
			res = portasignatureCallback.testDocumentPortasignatures(383147230, PortasignaturesCallback.DOCUMENT_REBUTJAT);
			// ERROR: Error procés petició callback portasignatures (id=383147230, estat=3, resposta=-1.0): null
			resultado = res == new Double(-1.0);
			assertTrue("Error al obtener la respuesta del portasignatures: PortasignaturesCallback.DOCUMENT_REBUTJAT ", resultado);

		} catch (Exception ex) {
			assertTrue("Error al obtener la respuesta del portasignatures", resultado);
		}
	}
	
	@Test
	//Elimina varios documentos del Portasignatures.
	public void g_deleteDocuments() throws PortasignaturesPluginException {
		
		b_stub();
		
		try {
			// Borramos 5 documentos
			List<Integer> documents = new ArrayList<Integer>();
			for (int i = 0 ; i < 5 ; i++) {
				documents.add(i);
			}
			DeleteRequest request = new DeleteRequest(
					getRequestApplication(),
					getDeleteRequestDocuments(documents), 
					null, 
					null);
			DeleteResponse response = stub.deleteDocuments(request);
			if (response.getResult().getCode() != 0) {
				throw new PortasignaturesPluginException("Error al eliminar el document del portasignatures: la resposta ha retornat l'error " + response.getResult().getMessage());
			}
		} catch (Exception ex) {
			throw new PortasignaturesPluginException("Error al eliminar el document al portasignatures", ex);
		}
	}

	private DeleteRequestDocument[] getDeleteRequestDocuments(
			List<Integer> documents) {
		DeleteRequestDocument[] documentsRequest = new DeleteRequestDocument[documents.size()];
		int i = 0;
		for (Integer document: documents) {
			DeleteRequestDocument documentRequest = new DeleteRequestDocument(document);
			documentsRequest[i++] = documentRequest; 
		}
			return documentsRequest;
	}

	private UploadRequestDocument getRequestDocument() throws IOException {
		UploadRequestDocument document1 = new UploadRequestDocument();
		DocumentAttributes attributes = new DocumentAttributes();

		// Elementos requeridos
		attributes.setTitle("Documento de test");
		attributes.setExtension("pdf");
		attributes.setDescription("Documento de test para firmar");
		// Queremos que se firmen los anexos
		attributes.setSignAnnexes(Boolean.FALSE);
		attributes.setExternalData("123459");

		// Elementos no requeridos
		// Informaci�n adicional del responsable del documento

		attributes.setSender(new Sender("12345678Z", "javierg@limit.es"));
		// url con información extra sobre el documento
		// attributes.setUrl("");

		// Fecha limite de firma del documento.
		// Es meramente informativa, no conlleva m�s que un aviso visual al usuario (rojo y cursiva)
		// Si no se especifica se le aplica un valor por defecto definido en un par�metro en BBDD.
		Calendar cal = Calendar.getInstance();
		// Los meses se situan en el segundo par�metro
		// Enero corresponde al mes 0.
		cal.set(2012, 12, 14);
		attributes.setDateLimit(cal);

		// Especificamos la importancia del documento.
		attributes.setImportance(ImportanceEnum.normal);
		// El tipo de documento al que pertenece.
		attributes.setType(Integer.valueOf("999"));
		document1.setAttributes(attributes);

		// Definimos la primera etapa con un firmante.
		// Se requiere que el DNI del certificado coincida con el DNI del usuario logado en Portafirmas
		// (true en el cuarto par�metro del constructor Signer)
		UploadStep step1 = new UploadStep();
		step1.setMinimalSigners(new Integer(1));
		step1.setSigners(new Signer[] { new Signer("12345678Z", "u04090", "javierg@limit.es", Boolean.TRUE, null, null, null, null, null, null, null, null, null) });

		// Agregamos las etapas al documento y especificamos que la firma es separada
		document1.setSteps(new Steps(SignModeEnum.attached, new UploadStep[] { step1 }));

		afegirAttachmentSoap();

		return document1;
	}
	
	private void afegirAttachmentSoap() throws IOException  {
		String filename = carregarPropietatPath("deploy.arxiu.portasignatures", "No se encontró el fichero a enviar al Portasignaturas");
		FileDataSource ds = new FileDataSource(filename);

		byte[] array = IOUtils.toByteArray(ds.getInputStream());
		DataHandler attachmentFile = new DataHandler(new ByteArrayDataSource(array, "application/octet-stream"));
		b_stub();
		stub.addAttachment(attachmentFile);
	}

	private Application getRequestApplication() {
		Application application = new Application();
		application.setUser(getUserName());
		application.setPassword(getPassword());
		return application;
	}

	private String getUserName() {
		return properties.getProperty("app.portasignatures.plugin.usuari");
	}

	private String getPassword() {
		return properties.getProperty("app.portasignatures.plugin.password");
	}
}
