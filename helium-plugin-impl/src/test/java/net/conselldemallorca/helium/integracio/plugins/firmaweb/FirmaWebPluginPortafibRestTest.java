package net.conselldemallorca.helium.integracio.plugins.firmaweb;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import net.conselldemallorca.helium.integracio.plugins.SistemaExternException;


public class FirmaWebPluginPortafibRestTest {

	private static final String classe = "org.fundaciobit.apisib.apifirmasimple.v1.ApiFirmaWebSimple";
	private static final String descripcioCurta ="Plugin de firma que usa PortaFIB API REST Simple";
	private static final String nom ="Firma dins PortaFIB amb API REST Sïmple";
	public static final String CONTEXTWEB = "/v3/firmapassarela";

	@BeforeClass
	public static void setUp() throws IOException {
		System.setProperty(
				"validatesignature.afirmacxf.ignoreservercertificates",
				"true");
		System.setProperty(
				"app.plugins.validatesignature.afirmacxf.ignoreservercertificates",
				"true");
		System.setProperty(
				"app.plugin.firma.portafib.username",
				"afirmades-firma");
		
	}
	 
	FirmaWebPluginPortafibRest plugin = new FirmaWebPluginPortafibRest(
			nom,
			descripcioCurta,
			classe,
			new Properties(),
			1);
		
	@Test
	public void test() {
		String annexId = "annexId";
		String fitxerNom = "annex_sense_firma.pdf";
		String motiu = "Prova firma en web";
		String mime = "application/pdf";
		String tipusDocumental = null;
		byte[] contingut = this.getContingut("/" + fitxerNom);
		try {
			String redirectUrl = plugin.firmar(annexId, fitxerNom, motiu, contingut, mime, tipusDocumental,CONTEXTWEB, "99999999R");
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
