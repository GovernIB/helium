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
public class PortasignaturesPluginPortafibTest {

	private static final String BASE_URL = "https://proves.caib.es/portafib";
	private static final String USERNAME = "$ripea_portafib";
	private static final String PASSWORD = "ripea_portafib";
	private static final String DESTINATARI = "43110511R";
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
				"app.portasignatures.plugin.portafib.log.actiu",
				"true");
		portasignaturesPlugin = new PortasignaturesPluginPortafib();
	}

	@Test
	public void enviarICancelar() throws PortasignaturesPluginException, IOException {
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

	private InputStream getDocumentTest() {
		InputStream is = getClass().getResourceAsStream(
        		"/net/conselldemallorca/helium/integracio/plugins/portasignatures/portafirmes_test.pdf");
		return is;
	}

}
