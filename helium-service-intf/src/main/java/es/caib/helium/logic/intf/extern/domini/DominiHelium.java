/**
 * 
 */
package es.caib.helium.logic.intf.extern.domini;

import javax.jws.WebService;
import java.util.List;


/**
 * Interfície que a implementar pels serveis web que retornin
 * informació d'un domini
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@WebService(targetNamespace="http://domini.integracio.helium.conselldemallorca.net/")
public interface DominiHelium {
	public List<FilaResultat> consultaDomini(String id, List<ParellaCodiValor> parametres) throws Exception;

}
