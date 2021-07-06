/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.custodia;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import es.caib.helium.logic.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.signatura.RespostaValidacioSignatura;

/**
 * Implementació mock del plugin de custodia documental que simula guardar
 * les signatures a dins l'aplicació de custòdia.
 * Per simular que guarda el contingut crea el fitxer amb el contingut al directori temporal
 * on s'estigui executant helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CustodiaPluginMock implements CustodiaPlugin {
	
	private static String tempDir = System.getProperty("java.io.tmpdir") + File.separator + "custodiaPluginMoch";

	public String addSignature(
			String id,
			String gesdocId,
			String arxiuNom,
			String tipusDocument,
			byte[] signatura) throws CustodiaPluginException {
		
		if ("true".equals(GlobalProperties.getInstance().getProperty("app.custodia.plugin.mock.custodiar"))) {
			
			// Ruta al directori pel fitxer
			File path = new File(tempDir);
			path.mkdirs();
			// Ruta destí a la carpeta selenium del directori temporal
			String filePath = tempDir + File.separator + id;		
			// Escriu el contingut a l'arxiu
			InputStream stream = null;
	        OutputStream resStreamOut = null;
	        try {
	        	stream = new ByteArrayInputStream(signatura);
	            int readBytes;
		        byte[] buffer = new byte[4096];
	            resStreamOut = new FileOutputStream(filePath);
	            while ((readBytes = stream.read(buffer)) > 0) {
	                resStreamOut.write(buffer, 0, readBytes);
	            }
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
	        	try {
	        		if (stream != null)
	        			stream.close();
	        		if (resStreamOut != null)
	        			resStreamOut.close();
	        	} catch(Exception e) {
	        		e.printStackTrace();
	        	}
			}
			return id;
		} else
			throw new CustodiaPluginException("CUSTODIA ERROR MOCK");
		
	}

	public List<byte[]> getSignatures(String id) throws CustodiaPluginException {

		List<byte[]> result = new ArrayList<byte[]>();
		String filePath = tempDir + File.separator + id;
		File file = new File(filePath);
		if (file.exists()) 
			try {
				result.add(Files.readAllBytes(file.toPath()));
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Error llegint el contingut del document pel portasignaturesPuginMock amb documentId " + id);
			}
		return result;		
	}


	public String getUrlComprovacioSignatura(
			String id) throws CustodiaPluginException {
		return "http://oficina.limit.es/";
	}


	@Override
	public byte[] getSignaturesAmbArxiu(String id) throws CustodiaPluginException {
		List<byte[]> signatures = getSignatures(id);
		if (!signatures.isEmpty())
			return signatures.get(0);
		else 
			return new byte[100];
	}

	@Override
	public void deleteSignatures(String id) throws CustodiaPluginException {
		String filePath = tempDir + File.separator + id;
		File file = new File(filePath);
		if (file.exists())
			file.delete();
	}

	@Override
	public List<RespostaValidacioSignatura> dadesValidacioSignatura(String id) throws CustodiaPluginException {
		return null;
	}

	@Override
	public boolean potObtenirInfoSignatures() {
		return false;
	}

	@Override
	public boolean isValidacioImplicita() {
		return false;
	}

}
