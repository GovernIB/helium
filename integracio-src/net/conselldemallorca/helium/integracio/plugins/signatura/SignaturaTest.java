/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.signatura;

import java.io.FileInputStream;
import java.io.FileOutputStream;



/**
 * Classe de test pel plugin de signatura
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class SignaturaTest {

	public static void main(String[] args) {
		SignaturaPlugin signaturaPlugin = new SignaturaPluginCaib();
		try {
			signaturaPlugin.testSigner();
			String[] certList = null;
			try {
				certList = signaturaPlugin.getCertList("");
			} catch (Exception ex) {
				ex.printStackTrace(); 
				return;
		    }
			if (certList != null) {
				for (String cert: certList) {
					System.out.println(">>> " + cert);
				}
			}
			byte[] resposta = (byte[])signaturaPlugin.sign(
					new FileInputStream("c:/signat1.pdf"),
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

}
