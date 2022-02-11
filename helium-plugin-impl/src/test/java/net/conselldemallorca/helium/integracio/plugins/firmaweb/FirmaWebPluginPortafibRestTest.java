package net.conselldemallorca.helium.integracio.plugins.firmaweb;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.fundaciobit.apisib.apifirmasimple.v1.ApiFirmaEnServidorSimple;
import org.fundaciobit.apisib.apifirmasimple.v1.ApiFirmaWebSimple;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleGetTransactionStatusResponse;
import org.fundaciobit.apisib.apifirmasimple.v1.jersey.ApiFirmaWebSimpleJersey;
import org.junit.Test;

import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.SistemaExternException;
import net.conselldemallorca.helium.integracio.plugins.firma.FirmaResposta;

public class FirmaWebPluginPortafibRestTest {

	private static final String endpoint = "https://proves.caib.es/portafib/common/rest/apifirmawebsimple/v1/";
	private static final String username = "$helium_portafib";
	private static final String password = "helium_portafib";
	private static final String classe = "org.fundaciobit.apisib.apifirmasimple.v1.ApiFirmaWebSimple";
	private static final String descripcioCurta ="Plugin de firma que usa PortaFIB API REST Simple";
	private static final String nom ="Firma dins PortaFIB amb API REST Sïmple";

	 
	FirmaWebPluginPortafibRest plugin = new FirmaWebPluginPortafibRest(
			nom,
			descripcioCurta,
			classe,
			new Properties());
		
	@Test
	public void test() {
		String annexId = "annexId";
		String fitxerNom = "annex_sense_firma.pdf";
		String motiu = "Prova firma en web";
		String mime = "application/pdf";
		String tipusDocumental = null;
		byte[] contingut = this.getContingut("/" + fitxerNom);
		try {
			FirmaSimpleGetTransactionStatusResponse redirectUrl = plugin.firmar(annexId, fitxerNom, motiu, contingut, mime, tipusDocumental);
			System.out.println("redirectUrl: " + redirectUrl);
		} catch (SistemaExternException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private byte[] getContingut(String fitxerNom) {
		byte[] contingut;
		try {
			InputStream arxiuContingut = getClass().getResourceAsStream(fitxerNom);
			contingut = IOUtils.toByteArray(arxiuContingut);
		} catch(Exception e) {
			throw new RuntimeException("Error obtenint el contingut de " + fitxerNom + ": " + e.getClass() + ": " + e.getMessage(), e);
		}
		return contingut;
	}
	

}
