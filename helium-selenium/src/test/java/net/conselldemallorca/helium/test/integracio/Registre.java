package net.conselldemallorca.helium.test.integracio;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.activation.FileDataSource;

import net.conselldemallorca.helium.integracio.plugins.registre.DadesAssumpte;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesExpedient;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesInteressat;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesNotificacio;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesOficina;
import net.conselldemallorca.helium.integracio.plugins.registre.DocumentRegistre;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreEntrada;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreNotificacio;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistrePluginException;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaAnotacioRegistre;
import net.conselldemallorca.helium.test.integracio.utils.RegistroPlugin;
import net.conselldemallorca.helium.test.util.BaseTest;

import org.apache.commons.io.IOUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Registre extends BaseTest {
	
	@Test
	// Registrarl la entrada del documento
	public void a_entrada() {
		RegistreEntrada registreEntrada = new RegistreEntrada();
		DadesOficina dadesOficina = new DadesOficina();
		dadesOficina.setOficinaCodi("1-1");
		dadesOficina.setOrganCodi("6");
		registreEntrada.setDadesOficina(dadesOficina);
		DadesInteressat dadesInteressat = new DadesInteressat();
		dadesInteressat.setNomAmbCognoms("Nombre");
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
		doc.setArxiuContingut(getDoc());
		documents.add(doc);

		registreEntrada.setDocuments(documents);
		RespostaAnotacioRegistre resposta;
		try {
			resposta = getRegistrePlugin().registrarEntrada(registreEntrada);
		} catch (RegistrePluginException e) {
			resposta = null;
		}
		assertFalse("Error al registrar la entrada del documento", resposta == null || resposta.getNumero() == null);
	}

	@Test
	// Registrar la salida o notificación del documento
	public void b_notificacio() {
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
		dadesInteressat.setNomAmbCognoms("Nom");
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

		doc.setArxiuContingut(getDoc());
		documents.add(doc);
		registreNotificacio.setDocuments(documents);
		RespostaAnotacioRegistre resposta;
		try {
			resposta = getRegistrePlugin().registrarNotificacio(registreNotificacio);
		} catch (RegistrePluginException e) {
			resposta = null;
		}
		assertFalse("Error al registrar la salida o notificación del documento", resposta == null || resposta.getNumero() == null);
	}

	private RegistroPlugin getRegistrePlugin() {
		return new RegistroPlugin();
	}

	private byte[] getDoc() {
		String filename = carregarPropietatPath("deploy.arxiu.registre", "No se encontró el fichero a enviar al registre");
		FileDataSource ds = new FileDataSource(filename);
		try {
			return IOUtils.toByteArray(ds.getInputStream());
		} catch (IOException e) {
			return "Bon dia".getBytes();
		}
	}
}
