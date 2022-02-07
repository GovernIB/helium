package net.conselldemallorca.helium.rest.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import es.caib.portafib.callback.beans.v1.PortaFIBEvent;
import es.caib.portafib.callback.beans.v1.SigningRequest;

/**
 * Prova de crida a l'API REST del callback del portafirmes.
 * 
 */
public class PortaFIBCallbackRestTest {

	private static final String URL_REST_BASE = "http://localhost:8080/helium/rest/portafib/callback";

	public static void main(String[] args) {

		try {
			// Estats:
			//   0  - DOCUMENT_PENDENT;
			//   50 - DOCUMENT_PENDENT;
			//   60 - DOCUMENT_FIRMAT;
			//   70 - DOCUMENT_REBUTJAT;
			//   80 - DOCUMENT_PAUSAT;
			long psignaDocumentId = 844735;
			int estat = 60;

			PortaFIBEvent event = new PortaFIBEvent();
			SigningRequest signingRequest = new SigningRequest();
			signingRequest.setID(psignaDocumentId);
			signingRequest.setState(3);
			signingRequest.setRejectionReason("Raó rebuig");
			event.setSigningRequest(signingRequest);
			event.setEventTypeID(estat);
			
			// Crida a l'API REST callback del portasignatures d'Helium
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(event);
			
			Client client = Client.create();
			WebResource webResource = client.resource(URL_REST_BASE + "/event");
			ClientResponse response = webResource.type("application/json").post(ClientResponse.class, json);

			System.out.println("Response status: " + response.getStatus());
			System.out.println("Resopnse entity: " + response.getEntity(String.class));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
