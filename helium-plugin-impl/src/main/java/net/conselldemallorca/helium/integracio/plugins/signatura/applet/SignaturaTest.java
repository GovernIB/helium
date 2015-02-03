/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.signatura.applet;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * Classe de test pel plugin de signatura
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SignaturaTest {

	public static void main(String[] args) {
		SignaturaAppletCaib signaturaPlugin = new SignaturaAppletCaib();
		try {
			signaturaPlugin.initSigner();
			String[] certList = null;
			try {
				certList = signaturaPlugin.getCertList("");
			} catch (Exception ex) {
				ex.printStackTrace(); 
				return;
		    }
			if (certList != null) {
				for (String cert: certList) {
					logger.info(">>> " + cert);
				}
			}
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			InputStream is = new FileInputStream("c:/signat1.pdf");
			int nRead;
			byte[] tmp = new byte[1024];
			while ((nRead = is.read(tmp, 0, tmp.length)) != -1) {
				buffer.write(tmp, 0, nRead);
			}
			buffer.flush();
			is.close();
			byte[] resposta = (byte[])signaturaPlugin.sign(
					buffer.toByteArray(),
					certList[0],
					"12341234",
					"");
			FileOutputStream out = new FileOutputStream("c:/signat2.pdf");
			out.write(resposta);
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static final Log logger = LogFactory.getLog(SignaturaTest.class);
}
