/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.registre;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.core.util.GlobalProperties;

import org.springframework.core.io.FileSystemResource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Test plugin registre
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistrePluginTest {

	public static void main(String[] args) throws Exception {
		try {
			new GlobalProperties(new FileSystemResource("/home/likewise-open/LIMIT_CECOMASA/josepg/Feina/config/helium.properties"));
			RegistrePluginTest test = new RegistrePluginTest();
			//test.notificacio();
			//test.nomOficina();
			test.entrada();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void entrada() throws Exception {
		establirUsuariAutenticat();
		RegistreEntrada registreEntrada = new RegistreEntrada();
		DadesOficina dadesOficina = new DadesOficina();
		dadesOficina.setOficinaCodi("1-1");
		dadesOficina.setOrganCodi("6");
		registreEntrada.setDadesOficina(dadesOficina);
		DadesInteressat dadesInteressat = new DadesInteressat();
		dadesInteressat.setNomAmbCognoms("Josep Gayà Proves");
		dadesInteressat.setMunicipiNom("Tegucigalpa");
		registreEntrada.setDadesInteressat(dadesInteressat);
		DadesAssumpte dadesAssumpte = new DadesAssumpte();
		dadesAssumpte.setIdiomaCodi("2");
		dadesAssumpte.setTipus("OF");
		dadesAssumpte.setAssumpte("123 provant 123");
		registreEntrada.setDadesAssumpte(dadesAssumpte);
		List<DocumentRegistre> documents = new ArrayList<DocumentRegistre>();
		DocumentRegistre doc = new DocumentRegistre();
		doc.setNom("Notificació correcció deficiències documentació");
		doc.setData(new Date());
		doc.setIdiomaCodi("ca");
		doc.setArxiuNom("Notificació correcció deficiències documentació(10).doc");
		doc.setArxiuContingut("Bon dia".getBytes());
		documents.add(doc);
		registreEntrada.setDocuments(documents);
		RespostaAnotacioRegistre resposta = getRegistrePlugin().registrarEntrada(registreEntrada);
		System.out.println(">>> num: " + resposta.getNumero());
	}

	public void notificacio() throws Exception {
		RegistreNotificacio registreNotificacio = new RegistreNotificacio();
		DadesExpedient dadesExpedient = new DadesExpedient();
		dadesExpedient.setIdentificador("13/2011");
		dadesExpedient.setClau("1304411104976");
		dadesExpedient.setUnitatAdministrativa("1");
		registreNotificacio.setDadesExpedient(dadesExpedient);
		DadesOficina dadesOficina = new DadesOficina();
		dadesOficina.setOrganCodi("1");
		dadesOficina.setOficinaCodi("3-1");
		registreNotificacio.setDadesOficina(dadesOficina);
		DadesInteressat dadesInteressat = new DadesInteressat();
		dadesInteressat.setAutenticat(true);
		dadesInteressat.setEntitatCodi(null);
		dadesInteressat.setNif("12345678Z");
		dadesInteressat.setNomAmbCognoms("Llorenç Mestre");
		dadesInteressat.setPaisCodi("es");
		dadesInteressat.setPaisNom("Espanya");
		dadesInteressat.setProvinciaCodi("07");
		dadesInteressat.setProvinciaNom("Illes Balears");
		dadesInteressat.setMunicipiCodi("033");
		dadesInteressat.setMunicipiNom("Manacor");
		registreNotificacio.setDadesInteressat(dadesInteressat);
		DadesNotificacio dadesNotifi = new DadesNotificacio();
		dadesNotifi.setIdiomaCodi("ca");
		dadesNotifi.setTipus("1");
		dadesNotifi.setAssumpte("Deficiència de la documentació enviada");
		dadesNotifi.setJustificantRecepcio(true);
		dadesNotifi.setAvisTitol("Notificació de deficiència");
		dadesNotifi.setAvisText("Voste ha rebut una notificacio telematica");
		dadesNotifi.setOficiTitol("Notificació de deficiència");
		dadesNotifi.setOficiText("Deficiència de la documentació enviada");
		registreNotificacio.setDadesNotificacio(dadesNotifi);
		List<DocumentRegistre> documents = new ArrayList<DocumentRegistre>();
		DocumentRegistre doc = new DocumentRegistre();
		doc.setNom("Notificació correcció deficiències documentació");
		doc.setData(new Date());
		doc.setIdiomaCodi("ca");
		doc.setArxiuNom("Notificació correcció deficiències documentació(10).doc");
		doc.setArxiuContingut(getResourceContent("test.doc"));
		documents.add(doc);
		registreNotificacio.setDocuments(documents);
		RespostaAnotacioRegistre resposta = getRegistrePlugin().registrarNotificacio(registreNotificacio);
		System.out.println(">>> num: " + resposta.getNumero());
	}

	public void nomOficina() throws Exception {
		System.out.println(">>> oficina: " + getRegistrePlugin().obtenirNomOficina("3-1"));
	}



	private RegistrePlugin getRegistrePlugin() {
		return new RegistrePluginRegwebWs();
	}

	private void establirUsuariAutenticat() {
		Authentication authentication =  new UsernamePasswordAuthenticationToken(
				new Principal() {
					public String getName() {
						return "josepg";
					}
				},
				null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	private byte[] getResourceContent(String resourceName) throws Exception {
		InputStream is = getClass().getResourceAsStream(resourceName);
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[16384];
		while ((nRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}
		buffer.flush();
		return buffer.toByteArray();
	}

}
