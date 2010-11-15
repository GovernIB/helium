package net.conselldemallorca.helium.integracio.plugins.portasignatures;

import java.util.Date;

import net.conselldemallorca.helium.integracio.plugins.persones.Persona;

/**
 * Plugin per a la integració amb portasignatures.
 * 
 * @author Miquel Angel Amengual <miquelaa@limit.es>
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
	public Integer UploadDocument (
			Persona persona,
			String arxiuNom,
			byte[] arxiuContingut,
			Integer tipusDocument,
			String expedient,
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
	public byte[] DownloadDocument(Integer documentId) throws PortasignaturesPluginException;

}
