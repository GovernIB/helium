/**
 * 
 */
package net.conselldemallorca.helium.ws.tramitacio.v1;

import java.util.Date;
import java.util.List;

import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * Interfície del servei de tramitació d'expedients de Helium
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@XmlSeeAlso({Object[].class, Object[][].class})
@WebService(
		name="TramitacioService",
		targetNamespace = "http://conselldemallorca.net/helium/ws/tramitacio/v1")
public interface TramitacioService {

	/**
	 * Mètode per a iniciar un expedient
	 * 
	 * @param entornCodi
	 * @param expedientTipus
	 * @param numero
	 * @param titol
	 * @param valorsFormulari
	 * @return La instància de procés de l'expedient creat.
	 * @throws TramitacioException
	 */
	public String iniciExpedient(
			String entornCodi,
			String expedientTipusCodi,
			String numero,
			String titol,
			List<ParellaCodiValor> valorsFormulari) throws TramitacioException;

	/**
	 * Mètode per a obtenir el llistat de tasques personals
	 * 
	 * @param entornCodi
	 * @param usuari
	 * @return El llistat de tasques
	 * @throws TramitacioException
	 */
	public List<TascaTramitacio> consultaTasquesPersonals(
			String entornCodi) throws TramitacioException;

	/**
	 * Mètode per a obtenir el llistat de tasques de grup
	 * 
	 * @param entornCodi
	 * @param usuari
	 * @return El llistat de tasques
	 * @throws TramitacioException
	 */
	public List<TascaTramitacio> consultaTasquesGrup(
			String entornCodi) throws TramitacioException;

	/**
	 * Mètode per a obtenir el llistat de tasques personals amb codi
	 * 
	 * @param entornCodi
	 * @param expedientNumero
	 * @return El llistat de tasques
	 * @throws TramitacioException
	 */
	public List<TascaTramitacio> consultaTasquesPersonalsByCodi(
			String entornCodi,
			String expedientNumero) throws TramitacioException;

	/**
	 * Mètode per a obtenir el llistat de tasques de grup amb codi
	 * 
	 * @param entornCodi
	 * @param expedientNumero
	 * @return El llistat de tasques
	 * @throws TramitacioException
	 */
	public List<TascaTramitacio> consultaTasquesGrupByCodi(
			String entornCodi,
			String expedientNumero) throws TramitacioException;

	/**
	 * Mètode per a obtenir el llistat de tasques personals donat
	 * l'identificador d'instància de procés.
	 * 
	 * @param entornCodi
	 * @param processInstanceId
	 * @return El llistat de tasques
	 * @throws TramitacioException
	 */
	public List<TascaTramitacio> consultaTasquesPersonalsByProces(
			String entornCodi,
			String processInstanceId) throws TramitacioException;

	/**
	 * Mètode per a obtenir el llistat de tasques de grup donat
	 * l'identificador d'instància de procés.
	 * 
	 * @param entornCodi
	 * @param processInstanceId
	 * @return El llistat de tasques
	 * @throws TramitacioException
	 */
	public List<TascaTramitacio> consultaTasquesGrupByProces(
			String entornCodi,
			String processInstanceId) throws TramitacioException;

	/**
	 * Mètode per a agafar una tasca assignada a un grup de l'usuari
	 * 
	 * @param entornCodi
	 * @param usuari
	 * @param tascaId
	 * @throws TramitacioException
	 */
	public void agafarTasca(
			String entornCodi,
			String tascaId) throws TramitacioException;

	/**
	 * Mètode per a alliberar una tasca assignada a un grup de l'usuari
	 * 
	 * @param entornCodi
	 * @param usuari
	 * @param tascaId
	 * @throws TramitacioException
	 */
	public void alliberarTasca(
			String entornCodi,
			String tascaId) throws TramitacioException;

	/**
	 * Mètode per a consultar els camps del formulari de la tasca
	 * 
	 * @param entornCodi
	 * @param usuari
	 * @param tascaId
	 * @throws TramitacioException
	 */
	public List<CampTasca> consultaFormulariTasca(
			String entornCodi,
			String tascaId) throws TramitacioException;

	/**
	 * Mètode per a modificar els valors del formulari de la tasca
	 * 
	 * @param entornCodi
	 * @param usuari
	 * @param tascaId
	 * @param valors
	 * @throws TramitacioException
	 */
	public void setDadesFormulariTasca(
			String entornCodi,
			String tascaId,
			List<ParellaCodiValor> valors) throws TramitacioException;

	/**
	 * Mètode per a consultar els documents de la tasca
	 * 
	 * @param entornCodi
	 * @param usuari
	 * @param tascaId
	 * @throws TramitacioException
	 */
	public List<DocumentTasca> consultaDocumentsTasca(
			String entornCodi,
			String tascaId) throws TramitacioException;

	/**
	 * Mètode per a modificar el contingut dels documents de la tasca
	 * 
	 * @param entornCodi
	 * @param usuari
	 * @param tascaId
	 * @param valors
	 * @throws TramitacioException
	 */
	public void setDocumentTasca(
			String entornCodi,
			String tascaId,
			String document,
			String nom,
			Date data,
			byte[] contingut) throws TramitacioException;

	/**
	 * Mètode per a esborrar el contingut dels documents de la tasca
	 * 
	 * @param entornCodi
	 * @param usuari
	 * @param tascaId
	 * @param document
	 * @throws TramitacioException
	 */
	public void esborrarDocumentTasca(
			String entornCodi,
			String tascaId,
			String document) throws TramitacioException;

	/**
	 * Mètode per a finalitzar una tasca
	 * 
	 * @param entornCodi
	 * @param usuari
	 * @param tascaId
	 * @param transicio
	 * @throws TramitacioException
	 */
	public void finalitzarTasca(
			String entornCodi,
			String tascaId,
			String transicio) throws TramitacioException;

	/**
	 * Mètode per a consultar la informació d'un expedient
	 * 
	 * @param entornCodiCodi
	 * @param processInstanceId
	 * @return La informació de l'expedient
	 * @throws TramitacioException
	 */
	public ExpedientInfo getExpedientInfo(
	    	String entornCodiCodi,
			String processInstanceId) throws TramitacioException;

	/**
	 * Consulta les variables d'un procés
	 * 
	 * @param entornCodi
	 * @param usuari
	 * @param processInstanceId
	 * @return
	 * @throws TramitacioException
	 */
	public List<CampProces> consultarVariablesProces(
			String entornCodi,
			String processInstanceId) throws TramitacioException;

	/**
	 * Mètode per a modificar variables del procés
	 * 
	 * @param entornCodi
	 * @param usuari
	 * @param processInstanceId
	 * @param varCodi
	 * @param valor
	 * @throws TramitacioException
	 */
	public void setVariableProces(
			String entornCodi,
			String processInstanceId,
			String varCodi,
			Object valor) throws TramitacioException;

	/**
	 * Mètode per a esborrar variables del procés
	 * 
	 * @param entornCodi
	 * @param usuari
	 * @param processInstanceId
	 * @param varCodi
	 * @throws TramitacioException
	 */
	public void esborrarVariableProces(
			String entornCodi,
			String processInstanceId,
			String varCodi) throws TramitacioException;

	/**
	 * Mètode per a consultar els documents d'un expedient
	 * 
	 * @param entornCodi
	 * @param usuari
	 * @param processInstanceId
	 * @return
	 * @throws TramitacioException
	 */
	public List<DocumentProces> consultarDocumentsProces(
			String entornCodi,
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
	 * @param processInstanceId
	 * @param documentCodi
	 * @param arxiu
	 * @param data
	 * @param contingut
	 * @return
	 * @throws TramitacioException
	 */
	public Long setDocumentProces(
			String entornCodi,
			String processInstanceId,
			String documentCodi,
			String arxiu,
			Date data,
			byte[] contingut) throws TramitacioException;

	/**
	 * Mètode per a esborrar documents del procés
	 * 
	 * @param entornCodi
	 * @param processInstanceId
	 * @param documentId
	 * @throws TramitacioException
	 */
	public void esborrarDocumentProces(
			String entornCodi,
			String processInstanceId,
			Long documentId) throws TramitacioException;

	/**
	 * Mètode per a executar una acció a dins un procés
	 * 
	 * @param entornCodi
	 * @param processInstanceId
	 * @param accio
	 * @throws TramitacioException
	 */
	public void executarAccioProces(
			String entornCodi,
			String processInstanceId,
			String accio) throws TramitacioException;	

	/**
	 * Mètode per a executar un script a dins un procés
	 * 
	 * @param entornCodi
	 * @param processInstanceId
	 * @param script
	 * @throws TramitacioException
	 */
	public void executarScriptProces(
			String entornCodi,
			String processInstanceId,
			String script) throws TramitacioException;

	/**
	 * Mètode per a aturar la tramitació d'un expedient
	 * 
	 * @param entornCodi
	 * @param processInstanceId
	 * @param motiu
	 * @throws TramitacioException
	 */
	public void aturarExpedient(
			String entornCodi,
			String processInstanceId,
			String motiu) throws TramitacioException;

	/**
	 * Mètode per a reprendre la tramitació d'un expedient
	 * 
	 * @param entornCodi
	 * @param processInstanceId
	 * @throws TramitacioException
	 */
	public void reprendreExpedient(
			String entornCodi,
			String processInstanceId) throws TramitacioException;

	/**
	 * Mètode per fer una consulta d'expedients
	 * 
	 * @param entornCodi
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
	 * @param processInstanceId
	 * @throws TramitacioException
	 */
	public void deleteExpedient(
			String entornCodi,
			String processInstanceId) throws TramitacioException;

}
