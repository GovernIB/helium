package net.conselldemallorca.helium.ms.domini.client.api;

import net.conselldemallorca.helium.ms.domini.client.DominiMsClient;
import net.conselldemallorca.helium.ms.domini.client.model.Domini;
import net.conselldemallorca.helium.ms.domini.client.model.DominiPagedList;

public class DominiMsTest {
	
	private static final String URL = "http://localhost:8082";
	private static final String USERNAME = null;
	private static final String PASSWORD = null;

	/** Prova simple d'ús de client REST del MS de Dominis. 
	 * 
	 * @param args
	 */
	public static void main(String[] args ) {
	
		// Instancia el client
		DominiMsClient dominis = new DominiMsClient(URL, USERNAME, PASSWORD);
		
		// Consulta un domini
		Long dominiId = 11760L;
		Domini domini = dominis.getDominiV1(dominiId);
		System.out.println(domini);
		
		// Consulta una llista de dominis
		Long entornId = 2L;
		String filtre = null;
		Long expedientTipusId = null;
		Long expedientTipusPareId = null;
		Integer page = null;
		Integer size = null;
		String sort = null;
		DominiPagedList dominisPagina = dominis.listDominisV1(entornId, filtre, expedientTipusId, expedientTipusPareId, page, size, sort);
		System.out.println("Pàgina de dominis: " + dominisPagina);
	}	
}
