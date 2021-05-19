package net.conselldemallorca.helium.ms.domini.client;

import java.util.Date;

import org.junit.Test;

import net.conselldemallorca.helium.ms.domini.client.model.Domini;
import net.conselldemallorca.helium.ms.domini.client.model.Domini.TipusEnum;
import net.conselldemallorca.helium.ms.domini.client.model.DominiPagedList;

public class DominiMsClientIT {
	
	private static final String URL = "http://localhost:8082";
	private static final String USERNAME = null;
	private static final String PASSWORD = null;

	/** Prova simple d'ús de client REST del MS de Dominis. 
	 * 
	 * @param args
	 */
	@Test
	public void llistat() {
	
		// Instancia el client
		DominiMsClient dominis = new DominiMsClient(URL, USERNAME, PASSWORD);
				
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
		DominiMsClient dominis = new DominiMsClient(URL, USERNAME, PASSWORD);
		
		// Consulta un domini
		Long dominiId = 11760L;
		Domini domini = dominis.getDominiV1(dominiId);
		System.out.println(domini);
	}
	
	@Test
	public void create_domini() {
		// Instancia el client
		DominiMsClient dominis = new DominiMsClient(URL, USERNAME, PASSWORD);
		
		// Creació d'un domini
		Domini newDomini = new Domini();
		newDomini.setEntornId(2L);
		newDomini.setCodi(String.valueOf(new Date().getTime()));
		newDomini.setNom(newDomini.getCodi());
		newDomini.setTipus(TipusEnum.WS);
		newDomini.setCacheSegons(0);
		Long newId = dominis.createDominiV1(newDomini);
		System.out.println("Domini creat amb ID: " + newId);
	}
	
	@Test
	public void delete_domini() {
		// Instancia el client
		DominiMsClient dominis = new DominiMsClient(URL, USERNAME, PASSWORD);
		
		// Creació d'un domini
		Long dominiId = 12550L;
		dominis.deleteDominiV1(dominiId);
		System.out.println("Domini esborrat amb ID: " + dominiId);
	}
}
