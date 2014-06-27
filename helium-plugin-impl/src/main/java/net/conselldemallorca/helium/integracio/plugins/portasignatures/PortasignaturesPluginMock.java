package net.conselldemallorca.helium.integracio.plugins.portasignatures;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
		return new ArrayList<byte[]>();
	}
	
	public void deleteDocuments (
			List<Integer> documents) throws PortasignaturesPluginException {
		
	}

}
