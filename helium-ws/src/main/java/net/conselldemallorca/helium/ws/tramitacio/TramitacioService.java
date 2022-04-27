/**
 * 
 */
package net.conselldemallorca.helium.ws.tramitacio;

import java.util.Date;
import java.util.List;

import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;

import net.conselldemallorca.helium.v3.core.api.dto.CampTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentTascaDto;

/**
 * Interfície del servei de tramitació d'expedients de Helium
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@XmlSeeAlso({Object[].class, Object[][].class})
@WebService(
		name="TramitacioService",
		targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/")
public interface TramitacioService {

	/**
	 * Mètode per a iniciar un expedient
	 * 
	 * @param entornCodi
	 * @param usuariCodi
	 * @param expedientTipusCodi
	 * @param numero
	 * @param titol
	 * @param valorsFormulari
	 * @return L'identificador intern de l'expedient
	 * @throws TramitacioException
	 */
	public String iniciExpedient(
			String entornCodi,
			String usuariCodi,
			String expedientTipusCodi,
			String numero,
			String titol,
			List<ParellaCodiValor> valorsFormulari) throws TramitacioException;

	/**
	 * Mètode per a obtenir el llistat de tasques personals
	 * 
	 * @param entornCodi
	 * @param usuariCodi
	 * @return El llistat de tasques
	 * @throws TramitacioException
	 */
	public List<TascaTramitacio> consultaTasquesPersonals(
			String entornCodi,
			String usuari) throws TramitacioException;

	/**
	 * Mètode per a obtenir el llistat de tasques de grup
	 * 
	 * @param entornCodi
	 * @param usuariCodi
	 * @return El llistat de tasques
	 * @throws TramitacioException
	 */
	public List<TascaTramitacio> consultaTasquesGrup(
			String entornCodi,
			String usuari) throws TramitacioException;

	/**
	 * Mètode per a obtenir el llistat de tasques personals donat el
	 * número d'expedient.
	 * 
	 * @param entornCodi
	 * @param usuariCodi
	 * @param expedientNumero
	 * @return El llistat de tasques
	 * @throws TramitacioException
	 */
	public List<TascaTramitacio> consultaTasquesPersonalsByCodi(
			String entornCodi,
			String usuariCodi,
			String expedientNumero) throws TramitacioException;

	/**
	 * Mètode per a obtenir el llistat de tasques de grup donat el
	 * número d'expedient.
	 * 
	 * @param entornCodi
	 * @param usuariCodi
	 * @param expedientNumero
	 * @return El llistat de tasques
	 * @throws TramitacioException
	 */
	public List<TascaTramitacio> consultaTasquesGrupByCodi(
			String entornCodi,
			String usuariCodi,
			String expedientNumero) throws TramitacioException;

	/**
	 * Mètode per a obtenir el llistat de tasques personals donat
	 * l'identificador d'instància de procés.
	 * 
	 * @param entornCodi
	 * @param usuariCodi
	 * @param processInstanceId
	 * @return El llistat de tasques
	 * @throws TramitacioException
	 */
	public List<TascaTramitacio> consultaTasquesPersonalsByProces(
			String entornCodi,
			String usuariCodi,
			String processInstanceId) throws TramitacioException;

	/**
	 * Mètode per a obtenir el llistat de tasques de grup donat
	 * l'identificador d'instància de procés.
	 * 
	 * @param entornCodi
	 * @param usuariCodi
	 * @param processInstanceId
	 * @return El llistat de tasques
	 * @throws TramitacioException
	 */
	public List<TascaTramitacio> consultaTasquesGrupByProces(
			String entornCodi,
			String usuariCodi,
			String processInstanceId) throws TramitacioException;

	/**
	 * Mètode per a agafar una tasca assignada a un grup de l'usuari
	 * 
	 * @param entornCodi
	 * @param usuariCodi
	 * @param tascaId
	 * @throws TramitacioException
	 */
	public void agafarTasca(
			String entornCodi,
			String usuariCodi,
			String tascaId) throws TramitacioException;

	/**
	 * Mètode per a alliberar una tasca assignada a un grup de l'usuari
	 * 
	 * @param entornCodi
	 * @param usuariCodi
	 * @param tascaId
	 * @throws TramitacioException
	 */
	public void alliberarTasca(
			String entornCodi,
			String usuariCodi,
			String tascaId) throws TramitacioException;

	/**
	 * Mètode per a consultar els camps del formulari de la tasca
	 * 
	 * @param entornCodi
	 * @param usuariCodi
	 * @param tascaId
	 * @throws TramitacioException
	 */
	public List<CampTascaDto> consultaFormulariTasca(
			String entornCodi,
			String usuariCodi,
			String tascaId) throws TramitacioException;

	/**
	 * Mètode per a modificar els valors del formulari de la tasca
	 * 
	 * @param entornCodi
	 * @param usuariCodi
	 * @param tascaId
	 * @param valorsFormulari
	 * @throws TramitacioException
	 */
	public void setDadesFormulariTasca(
			String entornCodi,
			String usuariCodi,
			String tascaId,
			List<ParellaCodiValor> valorsFormulari) throws TramitacioException;

	/**
	 * Mètode per a consultar els documents de la tasca
	 * 
	 * @param entornCodi
	 * @param usuariCodi
	 * @param tascaId
	 * @throws TramitacioException
	 */
	public List<DocumentTascaDto> consultaDocumentsTasca(
			String entornCodi,
			String usuariCodi,
			String tascaId) throws TramitacioException;

	/**
	 * Mètode per a modificar el contingut d'un document de la tasca
	 * 
	 * @param entornCodi
	 * @param usuariCodi
	 * @param tascaId
	 * @param documentCodi
	 * @param arxiuNom
	 * @param documentData
	 * @param arxiuContingut
	 * @throws TramitacioException
	 */
	public void setDocumentTasca(
			String entornCodi,
			String usuariCodi,
			String tascaId,
			String documentCodi,
			String arxiuNom,
			Date documentData,
			byte[] arxiuContingut) throws TramitacioException;

	/**
	 * Mètode per a esborrar un document de la tasca
	 * 
	 * @param entornCodi
	 * @param usuariCodi
	 * @param tascaId
	 * @param documentCodi
	 * @throws TramitacioException
	 */
	public void esborrarDocumentTasca(
			String entornCodi,
			String usuariCodi,
			String tascaId,
			String documentCodi) throws TramitacioException;

	/**
	 * Mètode per a finalitzar una tasca
	 * 
	 * @param entornCodi
	 * @param usuariCodi
	 * @param tascaId
	 * @param transicio
	 * @throws TramitacioException
	 */
	public void finalitzarTasca(
			String entornCodi,
			String usuariCodi,
			String tascaId,
			String transicio) throws TramitacioException;

	/**
	 * Mètode per a consultar la informació d'un expedient
	 * 
	 * @param entornCodi
	 * @param usuariCodi
	 * @param processInstanceId
	 * @return La informació de l'expedient
	 * @throws TramitacioException
	 */
	public ExpedientInfo getExpedientInfo(
			String entornCodi,
			String usuariCodi,
			String processInstanceId) throws TramitacioException;

	/**
	 * Consulta les variables d'un procés
	 * 
	 * @param entornCodi
	 * @param usuariCodi
	 * @param processInstanceId
	 * @return
	 * @throws TramitacioException
	 */
	public List<CampProces> consultarVariablesProces(
			String entornCodi,
			String usuariCodi,
			String processInstanceId) throws TramitacioException;

	/**
	 * Mètode per a modificar variables del procés
	 * 
	 * @param entornCodi
	 * @param usuariCodi
	 * @param processInstanceId
	 * @param varCodi
	 * @param varValor
	 * @throws TramitacioException
	 */
	public void setVariableProces(
			String entornCodi,
			String usuariCodi,
			String processInstanceId,
			String varCodi,
			Object varValor) throws TramitacioException;

	/**
	 * Mètode per a esborrar variables del procés
	 * 
	 * @param entornCodi
	 * @param usuariCodi
	 * @param processInstanceId
	 * @param varCodi
	 * @throws TramitacioException
	 */
	public void esborrarVariableProces(
			String entornCodi,
			String usuariCodi,
			String processInstanceId,
			String varCodi) throws TramitacioException;

	/**
	 * Mètode per a consultar els documents d'un expedient
	 * 
	 * @param entornCodi
	 * @param usuariCodi
	 * @param processInstanceId
	 * @return
	 * @throws TramitacioException
	 */
	public List<DocumentProces> consultarDocumentsProces(
			String entornCodi,
			String usuariCodi,
			String processInstanceId) throws TramitacioException;

	/**
	 * Mètode per a obtenir el contingut d'un document de l'expedient
	 * 
	 * @param documentId
	 * @return
	 * @throws TramitacioException
	 */
	public ArxiuProces getArxiuProces(
			Long documentId) throws TramitacioException;

	/**
	 * Mètode per a modificar documents del procés
	 * 
	 * @param entornCodi
	 * @param usuariCodi
	 * @param processInstanceId
	 * @param documentCodi
	 * @param arxiuNom
	 * @param documentData
	 * @param arxiuContingut
	 * @return
	 * @throws TramitacioException
	 */
	public void setDocumentProces(
			String entornCodi,
			String usuariCodi,
			String processInstanceId,
			String documentCodi,
			String arxiuNom,
			Date documentData,
			byte[] arxiuContingut) throws TramitacioException;

	/**
	 * Mètode per a esborrar documents del procés
	 * 
	 * @param entornCodi
	 * @param usuariCodi
	 * @param processInstanceId
	 * @param documentId
	 * @throws TramitacioException
	 */
	public void esborrarDocumentProces(
			String entornCodi,
			String usuariCodi,
			String processInstanceId,
			Long documentId) throws TramitacioException;

	/**
	 * Mètode per a executar una acció a dins un procés
	 * 
	 * @param entornCodi
	 * @param usuariCodi
	 * @param processInstanceId
	 * @param accio
	 * @throws TramitacioException
	 */
	public void executarAccioProces(
			String entornCodi,
			String usuariCodi,
			String processInstanceId,
			String accio) throws TramitacioException;	

	/**
	 * Mètode per a executar un script a dins un procés
	 * 
	 * @param entornCodi
	 * @param usuariCodi
	 * @param processInstanceId
	 * @param script
	 * @throws TramitacioException
	 */
	public void executarScriptProces(
			String entornCodi,
			String usuariCodi,
			String processInstanceId,
			String script) throws TramitacioException;

	/**
	 * Mètode per a aturar la tramitació d'un expedient
	 * 
	 * @param entornCodi
	 * @param usuariCodi
	 * @param processInstanceId
	 * @param motiu
	 * @throws TramitacioException
	 */
	public void aturarExpedient(
			String entornCodi,
			String usuariCodi,
			String processInstanceId,
			String motiu) throws TramitacioException;

	/**
	 * Mètode per a reprendre la tramitació d'un expedient
	 * 
	 * @param entornCodi
	 * @param usuariCodi
	 * @param processInstanceId
	 * @throws TramitacioException
	 */
	public void reprendreExpedient(
			String entornCodi,
			String usuariCodi,
			String processInstanceId) throws TramitacioException;

	/**
	 * Mètode per fer una consulta d'expedients
	 * 
	 * @param entornCodi
	 * @param usuariCodi
	 * @param titol
	 * @param numero
	 * @param dataInici1
	 * @param dataInici2
	 * @param expedientTipusCodi
	 * @param estatCodi
	 * @param iniciat
	 * @param finalitzat
	 * @param geoPosX
	 * @param geoPosY
	 * @param geoReferencia
	 * @throws TramitacioException
	 */
	public List<ExpedientInfo> consultaExpedients(
			String entornCodi,
			String usuariCodi,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			String expedientTipusCodi,
			String estatCodi,
			boolean iniciat,
			boolean finalitzat,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia) throws TramitacioException;

	/**
	 * Mètode per a esborrar un expedient
	 * 
	 * @param entornCodi
	 * @param usuariCodi
	 * @param processInstanceId
	 * @throws TramitacioException
	 */
	public void deleteExpedient(
			String entornCodi,
			String usuariCodi,
			String processInstanceId) throws TramitacioException;

}
