package net.conselldemallorca.helium.rest.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;

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
		PortaFIBCallbackRestTest.event();
		PortaFIBCallbackRestTest.versio();
	}

	private static void event() {
		// Callback event
		try {
			// Estats:
			//   0  - DOCUMENT_PENDENT;
			//   50 - DOCUMENT_PENDENT;
			//   60 - DOCUMENT_FIRMAT;
			//   70 - DOCUMENT_REBUTJAT;
			//   80 - DOCUMENT_PAUSAT;
			long psignaDocumentId = 1523483;
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
	
	private static void versio() {
		// Test versió
		try {
			String urlStr = URL_REST_BASE + "/versio";

		      int pos = urlStr.lastIndexOf("/");

		      urlStr = urlStr.substring(0, pos) + "/versio";

		      URL url = new URL(urlStr);
		      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		      conn.setRequestMethod("GET");
		      conn.setRequestProperty("Accept", "application/json");

		      if (conn.getResponseCode() != 200) {
		         throw new Exception("Failed : HTTP error code : " + conn.getResponseCode());
		      }

		      BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

		      String output = IOUtils.toString(br);

		      System.out.println("Testing OK. API WS PortaFIB v1. Usuari aplicació "
		            + " amb URL " + urlStr
		            + ". Cridada a getVersionWs() amb resultat " + output);

		      conn.disconnect();
		} catch(Exception e) {
			System.err.println("Error provant el mètode /versio: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
