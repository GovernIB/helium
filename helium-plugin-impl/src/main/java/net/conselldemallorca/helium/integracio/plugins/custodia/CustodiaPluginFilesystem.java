/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.custodia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.integracio.plugins.signatura.RespostaValidacioSignatura;
import net.conselldemallorca.helium.core.util.GlobalProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementació del plugin de custodia documental que guarda
 * les signatures en un fitxer
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CustodiaPluginFilesystem implements CustodiaPlugin {

	private static final String ID_PREFIX = "HELSIG_";



	public String addSignature(
			String gesdocId,
			String arxiuNom,
			String tipusDocument,
			byte[] signatura) throws CustodiaPluginException {
		try {
			File f = new File(getBaseDir() + ID_PREFIX + gesdocId);
			if (f.exists())
				f.delete();
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oout = new ObjectOutputStream(fos);
			oout.writeObject(signatura);
			fos.close();
			return gesdocId;
		} catch (Exception ex) {
			logger.error("No s'ha pogut custodiar la signatura", ex);
			throw new CustodiaPluginException("No s'ha pogut custodiar la signatura", ex);
		}
	}

	public List<byte[]> getSignatures(String id) throws CustodiaPluginException {
		try {
			List<byte[]> resposta = new ArrayList<byte[]>();
			File f = new File(getBaseDir() + ID_PREFIX + id);
			if (f.exists()) {
				FileInputStream fis = new FileInputStream(f);
				ObjectInputStream inputFromApplet = new ObjectInputStream(fis);
				resposta.add((byte[])inputFromApplet.readObject());
			}
			return resposta;
		} catch (Exception ex) {
			throw new CustodiaPluginException("No s'han pogut esborrar les signatures", ex);
		}
	}

	public byte[] getSignaturesAmbArxiu(String id) throws CustodiaPluginException {
		try {
			File f = new File(getBaseDir() + ID_PREFIX + id);
			if (f.exists()) {
				FileInputStream fis = new FileInputStream(f);
				ObjectInputStream inputFromApplet = new ObjectInputStream(fis);
				return (byte[])inputFromApplet.readObject();
			}
			return null;
		} catch (Exception ex) {
			logger.error("No s'ha pogut obtenir l'arxiu amb les signatures", ex);
			throw new CustodiaPluginException("No s'ha pogut obtenir l'arxiu amb les signatures", ex);
		}
	}

	public void deleteSignatures(String id) throws CustodiaPluginException {
		try {
			File f = new File(getBaseDir() + ID_PREFIX + id);
			if (f.exists())
				f.delete();
		} catch (Exception ex) {
			logger.error("No s'han pogut esborrar les signatures", ex);
			throw new CustodiaPluginException("No s'han pogut esborrar les signatures", ex);
		}
	}

	public List<RespostaValidacioSignatura> dadesValidacioSignatura(String id) throws CustodiaPluginException {
		return null;
	}

	public boolean potObtenirInfoSignatures() {
		return false;
	}
	public boolean isValidacioImplicita() {
		return false;
	}



	private String getBaseDir() {
		return GlobalProperties.getInstance().getProperty("app.custodia.plugin.filesystem.basedir");
	}

	private static final Log logger = LogFactory.getLog(CustodiaPluginFilesystem.class);

}
