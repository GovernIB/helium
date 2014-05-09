package net.conselldemallorca.helium.test.integracio.utils;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import net.conselldemallorca.helium.test.util.BaseTest;
import es.indra.www.portafirmasmcgdws.mcgdws.Attributes;
import es.indra.www.portafirmasmcgdws.mcgdws.AttributesState;
import es.indra.www.portafirmasmcgdws.mcgdws.CallbackRequest;
import es.indra.www.portafirmasmcgdws.mcgdws.Document;
import es.indra.www.portafirmasmcgdws.mcgdws.MCGDWSSoapBindingStub;
import es.indra.www.portafirmasmcgdws.mcgdws.MCGDwsService;
import es.indra.www.portafirmasmcgdws.mcgdws.MCGDwsServiceLocator;

public class PortasignaturesCallback extends BaseTest {
	public static final AttributesState DOCUMENT_BLOQUEJAT = AttributesState.value1; /** circuito de firmas bloqueado */
	public static final AttributesState DOCUMENT_PENDENT = AttributesState.value2; /** circuito de firmas incompleto */
	public static final AttributesState DOCUMENT_SIGNAT = AttributesState.value3; /** circuito de firmas finalizado */
	public static final AttributesState DOCUMENT_REBUTJAT = AttributesState.value4; /** circuito de firmas rechazado */ 
	
	public double testDocumentPortasignatures(int id, AttributesState state) throws Exception {
		String urlEndPoint =  properties.getProperty("app.portasignatures.plugin.url.callback");
		
		CallbackRequest callbackRequest = new CallbackRequest();
		es.indra.www.portafirmasmcgdws.mcgdws.Application app = new es.indra.www.portafirmasmcgdws.mcgdws.Application();
		Document document = new Document();
		document.setId(id); // Posar aquí l'id del document
		Attributes attributes = new Attributes();
		attributes.setTitle("Titol no buit");
		attributes.setState(state);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		attributes.setDateLastUpdate(cal);
		attributes.setExternalData("external data");
		attributes.setSignAnnexes(false);
		document.setAttributes(attributes);
		app.setDocument(document);
		callbackRequest.setApplication(app);
		MCGDwsService service = new MCGDwsServiceLocator();
		MCGDWSSoapBindingStub stub = (MCGDWSSoapBindingStub) service.getMCGDWS(new URL(urlEndPoint));

		return stub.callback(callbackRequest).get_return();
	}
}
