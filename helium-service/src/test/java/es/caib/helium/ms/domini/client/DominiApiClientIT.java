package es.caib.helium.ms.domini.client;

import es.caib.helium.ms.domini.client.model.Domini;
import es.caib.helium.ms.domini.client.model.DominiPagedList;
import org.junit.Test;

import java.util.Date;

public class DominiApiClientIT {
	
	private static final String URL = "http://localhost:8082";
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "admin";
	private static final boolean DEBUGGING = true;

	/** Prova simple d'ús de client REST del MS de Dominis. 
	 * 
	 * @param args
	 */
	@Test
	public void llistat() {
	
		// Instancia el client
		DominiApiClient dominis = new DominiApiClient(URL, USERNAME, PASSWORD, DEBUGGING);
				
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
	
	@Test
	public void get_domini() {
		// Instancia el client
		DominiApiClient dominis = new DominiApiClient(URL, USERNAME, PASSWORD, DEBUGGING);
		
		// Consulta un domini
		Long dominiId = 11760L;
		Domini domini = dominis.getDominiV1(dominiId);
		System.out.println(domini);
	}
	
	private static Long dominiId = null;
	
	@Test
	public void create_domini() {
		// Instancia el client
		DominiApiClient dominis = new DominiApiClient(URL, USERNAME, PASSWORD, DEBUGGING);
		
		// Creació d'un domini
		Domini newDomini = new Domini();
		newDomini.setEntornId(2L);
		newDomini.setCodi(String.valueOf(new Date().getTime()));
		newDomini.setNom(newDomini.getCodi());
		newDomini.setTipus(Domini.TipusEnum.CONSULTA_WS);
		newDomini.setUrl("http://localhost/ws");
		newDomini.setCacheSegons(0);
		Long newId = dominis.createDominiV1(newDomini);
		System.out.println("Domini creat amb ID: " + newId);
		DominiApiClientIT.dominiId = newId;
	}
	
	@Test
	public void delete_domini() {
		// Instancia el client
		DominiApiClient dominis = new DominiApiClient(URL, USERNAME, PASSWORD, DEBUGGING);
		
		// Creació d'un domini
		Long dominiId = DominiApiClientIT.dominiId != null ? DominiApiClientIT.dominiId : 12550L;
		dominis.deleteDominiV1(dominiId);
		System.out.println("Domini esborrat amb ID: " + dominiId);
	}
}
