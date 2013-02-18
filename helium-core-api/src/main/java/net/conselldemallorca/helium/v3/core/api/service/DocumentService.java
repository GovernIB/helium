/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.Date;

import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.exception.DefinicioProcesNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.DocumentGenerarException;
import net.conselldemallorca.helium.v3.core.api.exception.DocumentNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.TaskInstanceNotFoundException;


/**
 * Servei per a gestionar els documents dels expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentService {

	public static final String PREFIX_VAR_DOCUMENT = "H3l1um#document.";
	public static final String PREFIX_ADJUNT = "H3l1um#adjunt.";
	public static final String PREFIX_SIGNATURA = "H3l1um#signatura.";

	public DocumentDto getInfo(Long documentId);

	public ArxiuDto getArxiuPerMostrar(Long documentId);

	public Long guardarDocument(
			String processInstanceId,
			String documentCodi,
			Date documentData,
			String arxiuNom,
			byte[] arxiuContingut);

	public Long guardarAdjunt(
			String processInstanceId,
			String adjuntId,
			String adjuntTitol,
			Date adjuntData,
			String arxiuNom,
			byte[] arxiuContingut);

	public void guardarDadesRegistre(
			Long documentStoreId,
			String registreNumero,
			Date registreData,
			String registreOficinaCodi,
			String registreOficinaNom,
			boolean registreEntrada);

	public void esborrarDocument(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi);

	public ArxiuDto generarAmbPlantilla(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi,
			Date dataDocument,
			boolean forsarAdjuntarAuto) throws DefinicioProcesNotFoundException, DocumentNotFoundException, DocumentGenerarException, TaskInstanceNotFoundException;

}
