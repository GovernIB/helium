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
			String signatariId,
			String arxiuDescripcio,
			String arxiuNom,
			byte[] arxiuContingut,
			Integer tipusDocument,
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

}
