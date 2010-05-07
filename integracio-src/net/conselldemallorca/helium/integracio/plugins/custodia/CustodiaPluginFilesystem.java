/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.custodia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import net.conselldemallorca.helium.model.exception.CustodiaPluginException;
import net.conselldemallorca.helium.util.GlobalProperties;

/**
 * Implementació del plugin de custodia documental que guarda
 * les signatures en un fitxer
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class CustodiaPluginFilesystem implements CustodiaPlugin {

	private static final String ID_PREFIX = "HEL_";



	public String addSignedDocument(DocumentCustodia document) {
		try {
			String fid = ID_PREFIX + document.getId();
			File f = new File(getBaseDir() + fid);
			if (f.exists())
				f.delete();
			document.setSignedFileContent((byte[])document.getSignature());
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oout = new ObjectOutputStream(fos);
			oout.writeObject(document);
			fos.close();
			return fid;
		} catch (Exception ex) {
			throw new CustodiaPluginException("No s'ha pogut guardar el document", ex);
		}
	}

	public void deleteSignedDocument(String id) {
		File f = new File(getBaseDir() + id);
		if (f.exists())
			f.delete();
	}

	public DocumentCustodia getSignedDocument(String id) {
		try {
			File f = new File(getBaseDir() + id);
			if (f.exists()) {
				FileInputStream fis = new FileInputStream(f);
				ObjectInputStream inputFromApplet = new ObjectInputStream(fis);
				return (DocumentCustodia)inputFromApplet.readObject();
			}
		} catch (Exception ex) {
			throw new CustodiaPluginException("No s'ha pogut recuperar el document", ex);
		}
		return null;
	}

	public List<SignaturaInfo> verifyDocument(String id) {
		return null;
	}



	private String getBaseDir() {
		return GlobalProperties.getInstance().getProperty("app.custodia.plugin.filesystem.basedir");
	}

}
