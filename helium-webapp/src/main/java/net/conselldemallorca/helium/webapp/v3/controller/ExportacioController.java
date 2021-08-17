package net.conselldemallorca.helium.webapp.v3.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import net.conselldemallorca.helium.core.model.hibernate.Alerta;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.webapp.exportacio.ExpedientEstatTipusEnum;
import net.conselldemallorca.helium.webapp.exportacio.ExpedientExportacio;
import net.conselldemallorca.helium.webapp.exportacio.ExportacioHelper;

@Controller
public class ExportacioController {
	
	@Autowired
	private ExportacioHelper exportacioHelper;
	
	@RequestMapping(value = "/v3/exportar", method = RequestMethod.GET)
	public void exportar(HttpServletRequest request) throws Exception {
		
		try {
			System.out.println("Inici exportacio Expedients");
			long tempsInicial = System.currentTimeMillis();
			exportacioHelper.exportarExpedients();
			long tempsFinal = System.currentTimeMillis();
			System.out.println("Fi exportacio Expedients - Duracio: " +  (tempsFinal - tempsInicial) + " milliseconds");
	
			System.out.println("Inici exportacio Processos");
			tempsInicial = System.currentTimeMillis();
			exportacioHelper.exportarProcessos();
			tempsFinal = System.currentTimeMillis();
			System.out.println("Fi exportacio Processos - Duracio: " +  (tempsFinal - tempsInicial) + " milliseconds");

			System.out.println("Inici exportacio Tasques");
			tempsInicial = System.currentTimeMillis();
			exportacioHelper.exportarTasques();
			tempsFinal = System.currentTimeMillis();
			System.out.println("Fi exportacio Tasques - Duracio: " +  (tempsFinal - tempsInicial) + " milliseconds");
			
			System.out.println("Inici exportacio Responsables");
			tempsInicial = System.currentTimeMillis();
			exportacioHelper.exportarResponsables();
			tempsFinal = System.currentTimeMillis();
			System.out.println("Fi exportacio Responsables - Duracio: " +  (tempsFinal - tempsInicial) + " milliseconds");

			System.out.println("Inici exportacio Dades");
			tempsInicial = System.currentTimeMillis();
			exportacioHelper.exportarDades();
			tempsFinal = System.currentTimeMillis();
			System.out.println("Fi exportacio Dades - Duracio: " +  (tempsFinal - tempsInicial) + " milliseconds");
		} catch (Exception ex) {
			throw new Exception("Error en la exportacio", ex);
		}
	}
	
	
}
