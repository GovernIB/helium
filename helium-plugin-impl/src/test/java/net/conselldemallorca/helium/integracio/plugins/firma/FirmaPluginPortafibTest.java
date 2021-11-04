/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.firma;

import static org.junit.Assert.fail;

import java.io.InputStream;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * Test del plugin de firma simple del portasignatures.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FirmaPluginPortafibTest {

	private static final String ENDPOINT_ADDRESS = "https://dev.caib.es/portafib/common/rest/apifirmaenservidorsimple/v1/";
//	private static final String USERNAME = "$helium_portafibpass";
//	private static final String PASSWORD = "helium_portafibpass";
	private static final String USERNAME = "$distribucio_portafib";
	private static final String PASSWORD = "distribucio_portafib";
	private static final String PERFIL = "";
	

	private FirmaPluginPortafib plugin;

	/** Accepta els certificats i afegeix el protocol TLSv1.2.
	 * @throws Exception */
	@Before
	public void init() throws Exception {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
		    public X509Certificate[] getAcceptedIssuers(){return null;}
		    public void checkClientTrusted(X509Certificate[] certs, String authType){}
		    public void checkServerTrusted(X509Certificate[] certs, String authType){}
		}};

		// Install the all-trusting trust manager
		try {
		    SSLContext sc = SSLContext.getInstance("TLS");
		    sc.init(null, trustAllCerts, new SecureRandom());
		    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
		    System.err.println("Error ingorant certificats: " + e.getMessage());
		    e.printStackTrace();
		}		
		// Afegeix el protocol TLSv1.2
    	SSLContext context = SSLContext.getInstance("TLSv1.2");
    	context.init(null,null,null);
    	SSLContext.setDefault(context); 
	}
	
	@Before
	public void setUp() throws Exception {
		//GlobalProperties.getProperties().setLlegirSystem(false);
		System.setProperty(
				"app.plugin.firma.portafib.plugins.signatureserver.portafib.api_passarela_url",
				ENDPOINT_ADDRESS);
		System.setProperty(
				"app.plugin.firma.portafib.plugins.signatureserver.portafib.api_passarela_username",
				USERNAME);
		System.setProperty(
				"app.plugin.firma.portafib.plugins.signatureserver.portafib.api_passarela_password",
				PASSWORD);
		System.setProperty(
				"app.plugin.firma.portafib.plugins.signatureserver.portafib.api_passarela_perfil",
				PERFIL);
		plugin = new FirmaPluginPortafib();
	}

	@Test
	public void test() {
		try {
			String annexId = "annexId";
			String fitxerNom = "annex_sense_firma.pdf";
			String motiu = "Prova firma en servidor";
			String mime = "application/pdf";
			String tipusDocumental = null;
			byte[] contingut = this.getContingut("/" + fitxerNom);
			FirmaResposta resposta = plugin.firmar(
					annexId, 
					fitxerNom, 
					motiu, 
					contingut, 
					mime,
					tipusDocumental);
			
			System.out.println("contingut: " + resposta.getContingut().length + " b");
			System.out.println("tipus: " + resposta.getTipusFirmaEni());
			System.out.println("perfil: " + resposta.getPerfilFirmaEni());
		} catch(Throwable e) {
			fail("Error capturat: " + e.getClass() + ": " + e.getMessage());
			e.printStackTrace(System.err);
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
