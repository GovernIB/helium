/**
 * 
 */
package net.conselldemallorca.helium.integracio.tramitacio;

import java.util.Date;
import java.util.List;

import javax.jws.WebService;

/**
 * Interfície del servei de tramitació d'expedients de Helium
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@WebService
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

}
