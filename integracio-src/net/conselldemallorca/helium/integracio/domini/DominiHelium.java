/**
 * 
 */
package net.conselldemallorca.helium.integracio.domini;

import java.util.List;

import javax.jws.WebService;



/**
 * Interfície que a implementar pels serveis web que retornin
 * informació d'un domini
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@WebService
public interface DominiHelium {

	public List<FilaResultat> consultaDomini(String id, List<ParellaCodiValor> parametres) throws DominiHeliumException;

}
