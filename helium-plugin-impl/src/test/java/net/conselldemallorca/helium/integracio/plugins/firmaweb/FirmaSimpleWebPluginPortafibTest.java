package net.conselldemallorca.helium.integracio.plugins.firmaweb;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.FirmaResultatDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.Sexe;

/** Prova d'enviar a firmar, recollir la URL, obtenir el transaction ID i el document firmat. */
public class FirmaSimpleWebPluginPortafibTest {

	public static void main(String [] args) throws Exception {
		

		System.out.println("-Inici-");
		try {
			// Prepara el plugin
			Properties properties = new Properties();
			properties.put("app.plugin.passarelafirma.plugins.signatureweb.portafib.apifirmawebsimple.endpoint", "https://dev.caib.es/portafib/common/rest/apifirmawebsimple/v1/");
			properties.put("app.plugin.passarelafirma.plugins.signatureweb.portafib.apifirmawebsimple.username", "$helium_portafibpass");
			properties.put("app.plugin.passarelafirma.plugins.signatureweb.portafib.apifirmawebsimple.password", "****");
			properties.put("app.plugin.passarelafirma.plugins.signatureweb.portafib.apifirmawebsimple.perfil", "");

			FirmaWebPlugin plugin = new FirmaSimpleWebPluginPortafib(properties);
			
			// Envia a firmar
			ArxiuDto arxiu = new ArxiuDto(
					"prova.pdf",
					getContingut("/annex_sense_firma.pdf"),
					"application/pdf");
			PersonaDto persona = new PersonaDto("codi", "Nom", "Llinagte", "email@prova.com", Sexe.SEXE_DONA);
			persona.setDni("12345678Z");
			
			System.out.println("Iniciant la petició de firma per passarel·la web...");
			String urlFirma = plugin.firmaSimpleWebStart(
					String.valueOf(new Date().getTime()),
					arxiu, 
					"Motiu prova", 
					"Lloc firma", 
					persona, 
					"http://localhost/prova");
			
			System.out.println("Procés iniciat. La URL per procedir a la firma és la segúent: " + urlFirma);
			System.out.print("Procediu a firmar i un cop firmat o cancel·lat introduïu el paràmetre transactionalID de la URL de retorn i premeu INTRO a la consola: ");
			
			// Llegeix el transactional ID
			BufferedReader reader = new BufferedReader(
		            new InputStreamReader(System.in));
			String transactionID = reader.readLine();
			
			// Obté el resultat
			System.out.println("Recuperant el resultat amb transactionalID = " + transactionID + "...");
			FirmaResultatDto resultat = plugin.firmaSimpleWebEnd(transactionID);
			System.out.println("El resutlat és : " + resultat.getStatus() + " " + (resultat.getMsg() != null ? resultat.getMsg() : ""));
			
			// Guarda el document firmat
			if (resultat.getFitxerFirmatContingut() != null) {
				Path path = Paths.get("/tmp/" + resultat.getFitxerFirmatNom());
				System.out.println("Guardant el contingut firmat a " + path + "...");
				Files.write(path, resultat.getFitxerFirmatContingut());
				System.out.println("Contingut guardat a " + path);
			}			
		} catch(Exception e) {
			System.out.println("Error inesperat: " + e.getMessage());
			e.printStackTrace();
		}
		System.out.println("-Fi-");

	}
	
	private static byte[] getContingut(String fitxerNom) {
		byte[] contingut;
		try {
			InputStream arxiuContingut = FirmaSimpleWebPluginPortafibTest.class.getResourceAsStream(fitxerNom);
			contingut = IOUtils.toByteArray(arxiuContingut);
		} catch(Exception e) {
			throw new RuntimeException("Error obtenint el contingut de " + fitxerNom + ": " + e.getClass() + ": " + e.getMessage(), e);
		}
		return contingut;
	}
	

}
