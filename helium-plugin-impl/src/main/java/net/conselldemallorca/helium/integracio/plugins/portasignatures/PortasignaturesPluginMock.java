package net.conselldemallorca.helium.integracio.plugins.portasignatures;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * Implementació Mock del plugin de portasignatures.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PortasignaturesPluginMock implements PortasignaturesPlugin {

	/**
	 * Puja un document al Portasignatures.
	 * 
	 * @param persona
	 * @param documentDto
	 * @param expedient
	 * @param importancia
	 * @param dataLimit
	 * 
	 * @return Id del document al Portasignatures
	 * 
	 * @throws Exception
	 */
	public Integer uploadDocument (
			DocumentPortasignatures document,
			List<DocumentPortasignatures> annexos,
			boolean isSignarAnnexos,
			PasSignatura[] passesSignatura,
			String remitent,
			String importancia,
			Date dataLimit) throws PortasignaturesPluginException {
		
		try {
			FileUtils.writeByteArrayToFile(new File("C:\\DOCUMENTS_REVISIO\\" + document.getArxiuNom()), document.getArxiuContingut());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Integer(new Long(System.currentTimeMillis()).intValue());
	}

	/**
	 * Descarrega un document del Portasignatures
	 * 
	 * @param documentId
	 * 
	 * @return document
	 * 
	 * @throws Exception
	 */
	public List<byte[]> obtenirSignaturesDocument(
			Integer documentId) throws PortasignaturesPluginException {
		byte[] array1 = new byte[50];
//		byte[] array2 = new byte[50];
		List<byte[]> result = new ArrayList<byte[]>();
		result.add(array1);
//		result.add(array2);
		return result;
	}
	
	public void deleteDocuments (
			List<Integer> documents) throws PortasignaturesPluginException {
		
	}

}
