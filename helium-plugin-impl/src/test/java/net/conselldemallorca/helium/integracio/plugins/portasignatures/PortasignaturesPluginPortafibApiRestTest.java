/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.portasignatures;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test de la implementació del plugin d'enviament al Portasignatures per API REST 
 * simple.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PortasignaturesPluginPortafibApiRestTest {

	private static final String BASE_URL = "https://dev.caib.es/portafib";
	private static final String USERNAME = "$helium_portafib";
	private static final String PASSWORD = "helium_portafib";
	private static final String DESTINATARI = "e43631077p";
	private static final int DOC_TIPUS = 11;
	private static final String DOCUMENT_TITOL_PREFIX = "Test enviament HELIUM ";

	private static PortasignaturesPlugin portasignaturesPlugin;

	@BeforeClass
	public static void setUp() throws IOException {
		System.setProperty(
				"app.portasignatures.plugin.portafib.base.url",
				BASE_URL);
		System.setProperty(
				"app.portasignatures.plugin.portafib.username",
				USERNAME);
		System.setProperty(
				"app.portasignatures.plugin.portafib.password",
				PASSWORD);
		System.setProperty(
				"app.portasignatures.plugin.portafib.perfil",
				"");
		portasignaturesPlugin = new PortasignaturesPluginPortafibApiRest();
	}

	@Test
	public void enviarDocument() throws PortasignaturesPluginException, IOException {
		DocumentPortasignatures document = new DocumentPortasignatures();
		document.setTitol(
				DOCUMENT_TITOL_PREFIX + System.currentTimeMillis());
		document.setArxiuNom("portafirmes_test.pdf");
		document.setArxiuContingut(IOUtils.toByteArray(getDocumentTest()));
		document.setTipus(DOC_TIPUS);
		document.setSignat(false);
		document.setReference(new Long(System.currentTimeMillis()).toString());
		PasSignatura pasSignatura = new PasSignatura();
		pasSignatura.setSignataris(new String[] {DESTINATARI});
		pasSignatura.setMinSignataris(1);
		portasignaturesPlugin.uploadDocument(
				document,
				new ArrayList<DocumentPortasignatures>(),
				false,
				new PasSignatura[] {pasSignatura},
				"Helium test",
				null,
				null);
	}
	
	@Test
	public void multiplesPasosDestinatarisTest() {
		try {
			DocumentPortasignatures document = new DocumentPortasignatures();
			document.setTitol(
					DOCUMENT_TITOL_PREFIX + System.currentTimeMillis());
			document.setArxiuNom("portafirmes_test.pdf");
			document.setArxiuContingut(IOUtils.toByteArray(getDocumentTest()));
			document.setTipus(DOC_TIPUS);
			document.setSignat(false);
			document.setReference(new Long(System.currentTimeMillis()).toString());
			List<PasSignatura> pasos = new ArrayList<PasSignatura>();
			PasSignatura pasSignatura ;
			List<String> destinataris;
			for (int i = 1; i <=3; i++) {
				pasSignatura = new PasSignatura();
				destinataris = new ArrayList<String>();
				for(int j=0; j<i; j++) {
					//destinataris.add("destinatari_" + i + "_" + j);
					destinataris.add(DESTINATARI);
				}
				pasSignatura.setSignataris( destinataris.toArray(new String[0]));
				pasSignatura.setMinSignataris(1);
				pasos.add(pasSignatura);
			}
			portasignaturesPlugin.uploadDocument(
					document,
					new ArrayList<DocumentPortasignatures>(),
					false,
					pasos.toArray(new PasSignatura[0]),
					"Helium test",
					null,
					null);
		} catch(Exception e) { 
			String errMsg = "Error capturat enviant el document: " + e.getMessage();
			System.err.println(errMsg);
			e.printStackTrace();
			fail(errMsg);
		}
	}

	private InputStream getDocumentTest() {
		InputStream is = getClass().getResourceAsStream(
        		"/net/conselldemallorca/helium/integracio/plugins/portasignatures/portafirmes_test.pdf");
		return is;
	}

}
