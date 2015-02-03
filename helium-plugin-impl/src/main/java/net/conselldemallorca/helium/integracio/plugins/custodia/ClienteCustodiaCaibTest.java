/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.custodia;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;

import net.conselldemallorca.helium.core.util.GlobalProperties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

/**
 * Test del client de custòdia de la CAIB
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClienteCustodiaCaibTest {

	private ClienteCustodiaCaib clienteCustodia;

	public static void main(String[] args) {
		try {
			Resource resource = new UrlResource(System.getProperty("es.caib.helium.properties.path"));
			new GlobalProperties(resource);
			new ClienteCustodiaCaibTest().testAfegir();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void testHash() throws Exception {
		byte[] resposta = getClienteCustodia().reservarDocumento("12345678");
		String hash = new String(resposta);
		logger.info(">>> " + hash);
	}
	public void testAfegir() throws Exception {
		byte[] xml = getClienteCustodia().custodiarPDFFirmado(
				new ByteArrayInputStream(IOUtils.toByteArray(new FileInputStream("c:/signatura.pdf"))),
				"original.pdf",
				"3500",
				"HELIUM_COMINF_DOCAPRO1");
		CustodiaResponseCaib resposta = getClienteCustodia().parseResponse(xml);
		if (resposta.isError())
			throw new CustodiaPluginException("Error en la petició de custòdia: [" + resposta.getErrorCodi() + "] " + resposta.getErrorDescripcio());
	}



	private ClienteCustodiaCaib getClienteCustodia() {
		if (clienteCustodia == null) {
			clienteCustodia = new ClienteCustodiaCaib(
					GlobalProperties.getInstance().getProperty("app.custodia.plugin.caib.url"),
					GlobalProperties.getInstance().getProperty("app.custodia.plugin.caib.usuari"),
					GlobalProperties.getInstance().getProperty("app.custodia.plugin.caib.password"));
		}
		return clienteCustodia;
	}

	private static final Log logger = LogFactory.getLog(ClienteCustodiaCaibTest.class);
}
