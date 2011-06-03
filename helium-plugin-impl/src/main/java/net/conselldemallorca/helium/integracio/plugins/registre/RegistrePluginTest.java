/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.registre;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.core.util.GlobalProperties;

import org.springframework.core.io.FileSystemResource;

/**
 * Test plugin registre
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistrePluginTest {

	public static void main(String[] args) throws Exception {
		try {
			new GlobalProperties(new FileSystemResource("c:/tmp/helium/global.properties"));
			RegistrePluginTest test = new RegistrePluginTest();
			//test.notificacio();
			test.nomOficina();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void notificacio() throws Exception {
		RegistrePluginAjuntament registrePlugin = new RegistrePluginAjuntament();
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
		RespostaAnotacioRegistre resposta = registrePlugin.registrarNotificacio(registreNotificacio);
		System.out.println(">>> num: " + resposta.getNumero());
	}
	
	public void nomOficina() throws Exception {
		RegistrePluginAjuntament registrePlugin = new RegistrePluginAjuntament();
		System.out.println(">>> oficina: " + registrePlugin.obtenirNomOficina("3-1"));
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
