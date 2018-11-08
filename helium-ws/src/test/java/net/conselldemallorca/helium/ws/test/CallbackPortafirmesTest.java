/**
 * 
 */
package net.conselldemallorca.helium.ws.test;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;

import javax.xml.rpc.ServiceException;

import es.indra.www.portafirmasmcgdws.mcgdws.Application;
import es.indra.www.portafirmasmcgdws.mcgdws.Attributes;
import es.indra.www.portafirmasmcgdws.mcgdws.AttributesState;
import es.indra.www.portafirmasmcgdws.mcgdws.CallbackRequest;
import es.indra.www.portafirmasmcgdws.mcgdws.Document;
import es.indra.www.portafirmasmcgdws.mcgdws.MCGDws;
import es.indra.www.portafirmasmcgdws.mcgdws.MCGDwsService;
import es.indra.www.portafirmasmcgdws.mcgdws.MCGDwsServiceLocator;

/**
 * @author perep
 *
 */
public class CallbackPortafirmesTest {

	private static final String SERVICE_URL = "http://localhost:8080/helium/services/MCGDWS";
	private static final int psignaDocumentId = 60452;

	public static void main(String[] args) {
		try {
			new CallbackPortafirmesTest().testCallback();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void testCallback() throws MalformedURLException, ServiceException, RemoteException {
		CallbackRequest callbackRequest = new CallbackRequest();
		Application app = new Application();
		Document document = new Document();
		document.setId(Integer.valueOf(psignaDocumentId)); // Id del document llegit anteriorment
		Attributes attributes = new Attributes();
		attributes.setTitle("Titol no buit");
		attributes.setState(AttributesState.value3);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		attributes.setDateLastUpdate(cal);
		attributes.setExternalData("external data");
		attributes.setSignAnnexes(false);
		document.setAttributes(attributes);
		app.setDocument(document);
		callbackRequest.setApplication(app);
		// Crida al WS callback del portasignatures
		String urlEndPoint =  SERVICE_URL;
		MCGDwsService service = new MCGDwsServiceLocator();
		MCGDws ws = service.getMCGDWS(new URL(urlEndPoint));
		ws.callback(callbackRequest);
	}

}
