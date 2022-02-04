package net.conselldemallorca.helium.integracio.plugins.portasignatures;

import java.util.Date;
import java.util.List;

/**
 * Plugin per a la integració amb portasignatures.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PortasignaturesPlugin {

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
			Date dataLimit) throws PortasignaturesPluginException;

	/**
	 * Descarrega un document del Portasignatures
	 * 
	 * @param documentId
	 * 
	 * @return document
	 * 
	 * @throws Exception
	 */
	public List<byte[]> obtenirSignaturesDocument(Integer documentId) throws PortasignaturesPluginException;

	/**
	 * Elimina el document del portasignatures
	 * 
	 * @param document Referència a la petició al portasignatures.
	 * 
	 * @throws PortasignaturesPluginException
	 */
	public void deleteDocuments (Integer document) throws PortasignaturesPluginException;
}
