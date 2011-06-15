/**
 * 
 */
package net.conselldemallorca.helium.ws.tramitacio;

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
		targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/")
public interface TramitacioService {

	/**
	 * Mètode per a iniciar un expedient
	 * 
	 * @param entorn
	 * @param expedientTipus
	 * @param numero
	 * @param titol
	 * @param valors
	 * @return L'identificador intern de l'expedient
	 * @throws TramitacioException
	 */
	public String iniciExpedient(
			String entorn,
			String usuari,
			String expedientTipus,
			String numero,
			String titol,
			List<ParellaCodiValor> valorsFormulari) throws TramitacioException;

	/**
	 * Mètode per a obtenir el llistat de tasques personals
	 * 
	 * @param entorn
	 * @param usuari
	 * @return El llistat de tasques
	 * @throws TramitacioException
	 */
	public List<TascaTramitacio> consultaTasquesPersonals(
			String entorn,
			String usuari) throws TramitacioException;

	/**
	 * Mètode per a obtenir el llistat de tasques de grup
	 * 
	 * @param entorn
	 * @param usuari
	 * @return El llistat de tasques
	 * @throws TramitacioException
	 */
	public List<TascaTramitacio> consultaTasquesGrup(
			String entorn,
			String usuari) throws TramitacioException;

	/**
	 * Mètode per a agafar una tasca assignada a un grup de l'usuari
	 * 
	 * @param entorn
	 * @param usuari
	 * @param tascaId
	 * @throws TramitacioException
	 */
	public void agafarTasca(
			String entorn,
			String usuari,
			String tascaId) throws TramitacioException;

	/**
	 * Mètode per a consultar els camps del formulari de la tasca
	 * 
	 * @param entorn
	 * @param usuari
	 * @param tascaId
	 * @throws TramitacioException
	 */
	public List<CampTasca> consultaFormulariTasca(
			String entorn,
			String usuari,
			String tascaId) throws TramitacioException;

	/**
	 * Mètode per a modificar els valors del formulari de la tasca
	 * 
	 * @param entorn
	 * @param usuari
	 * @param tascaId
	 * @param valors
	 * @throws TramitacioException
	 */
	public void setDadesFormulariTasca(
			String entorn,
			String usuari,
			String tascaId,
			List<ParellaCodiValor> valors) throws TramitacioException;

	/**
	 * Mètode per a consultar els documents de la tasca
	 * 
	 * @param entorn
	 * @param usuari
	 * @param tascaId
	 * @throws TramitacioException
	 */
	public List<DocumentTasca> consultaDocumentsTasca(
			String entorn,
			String usuari,
			String tascaId) throws TramitacioException;

	/**
	 * Mètode per a modificar el contingut dels documents de la tasca
	 * 
	 * @param entorn
	 * @param usuari
	 * @param tascaId
	 * @param valors
	 * @throws TramitacioException
	 */
	public void setDocumentTasca(
			String entorn,
			String usuari,
			String tascaId,
			String document,
			String nom,
			Date data,
			byte[] contingut) throws TramitacioException;

	/**
	 * Mètode per a esborrar el contingut dels documents de la tasca
	 * 
	 * @param entorn
	 * @param usuari
	 * @param tascaId
	 * @param document
	 * @throws TramitacioException
	 */
	public void esborrarDocumentTasca(
			String entorn,
			String usuari,
			String tascaId,
			String document) throws TramitacioException;

	/**
	 * Mètode per a finalitzar una tasca
	 * 
	 * @param entorn
	 * @param usuari
	 * @param tascaId
	 * @param transicio
	 * @throws TramitacioException
	 */
	public void finalitzarTasca(
			String entorn,
			String usuari,
			String tascaId,
			String transicio) throws TramitacioException;

	/**
	 * Consulta les variables d'un procés
	 * 
	 * @param entorn
	 * @param usuari
	 * @param processInstanceId
	 * @return
	 * @throws TramitacioException
	 */
	public List<CampProces> consultarVariablesProces(
			String entorn,
			String usuari,
			String processInstanceId) throws TramitacioException;

	/**
	 * Mètode per a modificar variables del procés
	 * 
	 * @param entorn
	 * @param usuari
	 * @param processInstanceId
	 * @param varCodi
	 * @param valor
	 * @throws TramitacioException
	 */
	public void setVariableProces(
			String entorn,
			String usuari,
			String processInstanceId,
			String varCodi,
			Object valor) throws TramitacioException;

	/**
	 * Mètode per a esborrar variables del procés
	 * 
	 * @param entorn
	 * @param usuari
	 * @param processInstanceId
	 * @param varCodi
	 * @throws TramitacioException
	 */
	public void esborrarVariableProces(
			String entorn,
			String usuari,
			String processInstanceId,
			String varCodi) throws TramitacioException;

	/**
	 * Mètode per a consultar els documents d'un expedient
	 * 
	 * @param entorn
	 * @param usuari
	 * @param processInstanceId
	 * @return
	 * @throws TramitacioException
	 */
	public List<DocumentProces> consultarDocumentsProces(
			String entorn,
			String usuari,
			String processInstanceId) throws TramitacioException;

	/**
	 * Mètode per a modificar documents del procés
	 * 
	 * @param entorn
	 * @param usuari
	 * @param processInstanceId
	 * @param documentCodi
	 * @param arxiu
	 * @param data
	 * @param contingut
	 * @return
	 * @throws TramitacioException
	 */
	public Long setDocumentProces(
			String entorn,
			String usuari,
			String processInstanceId,
			String documentCodi,
			String arxiu,
			Date data,
			byte[] contingut) throws TramitacioException;

	/**
	 * Mètode per a esborrar documents del procés
	 * 
	 * @param entorn
	 * @param usuari
	 * @param processInstanceId
	 * @param documentId
	 * @throws TramitacioException
	 */
	public void esborrarDocumentProces(
			String entorn,
			String usuari,
			String processInstanceId,
			Long documentId) throws TramitacioException;

	/**
	 * Mètode per a executar una acció a dins un procés
	 * 
	 * @param entorn
	 * @param usuari
	 * @param processInstanceId
	 * @param accio
	 * @throws TramitacioException
	 */
	public void executarAccioProces(
			String entorn,
			String usuari,
			String processInstanceId,
			String accio) throws TramitacioException;	

	/**
	 * Mètode per a executar un script a dins un procés
	 * 
	 * @param entorn
	 * @param usuari
	 * @param processInstanceId
	 * @param script
	 * @throws TramitacioException
	 */
	public void executarScriptProces(
			String entorn,
			String usuari,
			String processInstanceId,
			String script) throws TramitacioException;

}
