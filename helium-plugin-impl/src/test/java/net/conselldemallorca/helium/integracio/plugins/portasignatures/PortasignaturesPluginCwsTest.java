/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.portasignatures;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test de la implementació de l'API de l'arxiu que utilitza
 * l'API REST de l'arxiu de la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PortasignaturesPluginCwsTest {

	private static final String DOCUMENT_TITOL_PREFIX = "Document test HELIUM ";
	private static final String DESTINATARI_FIRMA = "43110511R";

	private static PortasignaturesPlugin portasignaturesPlugin;

	@BeforeClass
	public static void setUp() throws IOException {
		System.setProperty(
				"app.portasignatures.plugin.url",
				"https://proves.caib.es/portafirmasws/web/services/CWS");
		System.setProperty(
				"app.portasignatures.plugin.usuari",
				"HELIUM_LIMIT");
		System.setProperty(
				"app.portasignatures.plugin.password",
				"HELIUM_LIMIT");
		System.setProperty(
				"app.portasignatures.plugin.signatura.tipus",
				"1");
		System.setProperty(
				"app.portasignatures.plugin.checkcerts",
				"false");
		portasignaturesPlugin = new PortasignaturesPluginCaib();
	}

	@Test
	public void enviarICancelar() throws PortasignaturesPluginException, IOException {
		DocumentPortasignatures document = new DocumentPortasignatures();
		document.setTitol(
				DOCUMENT_TITOL_PREFIX + System.currentTimeMillis());
		document.setArxiuNom("portafirmes_test.pdf");
		document.setArxiuContingut(IOUtils.toByteArray(getDocumentTest()));
		document.setSignat(false);
		document.setReference(new Long(System.currentTimeMillis()).toString());
		PasSignatura pasSignatura = new PasSignatura();
		pasSignatura.setSignataris(new String[] {DESTINATARI_FIRMA});
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

	private InputStream getDocumentTest() {
		InputStream is = getClass().getResourceAsStream(
        		"/net/conselldemallorca/helium/integracio/plugins/portasignatures/portafirmes_test.pdf");
		return is;
	}

}
