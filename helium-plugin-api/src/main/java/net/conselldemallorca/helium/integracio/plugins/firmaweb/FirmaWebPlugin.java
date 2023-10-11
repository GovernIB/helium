package net.conselldemallorca.helium.integracio.plugins.firmaweb;

import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.FirmaResultatDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;

/**
 * Interfície per accedir a la funcionalitat de la firma web del portasignatures.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface FirmaWebPlugin {

	/** Mètode per inicar una petició de firma web d'un arxiu.
	 * 
	 * @param arxiu
	 * @param motiu
	 * @param lloc 
	 * @param persona
	 * @param urlRetorn
	 * 
	 * @return URL on es redirigirà per a que l'usuari pugui firmar.
	 */
	public String firmaSimpleWebStart(ArxiuDto arxiu, String motiu, String lloc, PersonaDto persona, String urlRetorn);

	/** Mètode per finalitzar una petició i recuperar el resultat.
	 * 
	 * @param transactionID
	 * @return
	 */
	public FirmaResultatDto firmaSimpleWebEnd(String transactionID);

}
