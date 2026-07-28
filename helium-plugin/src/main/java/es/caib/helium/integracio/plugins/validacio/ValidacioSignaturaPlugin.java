package es.caib.helium.integracio.plugins.validacio;

import es.caib.helium.commons.exception.SistemaExternException;

/**
 * Interfície del plugin per a la validació de firmes.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ValidacioSignaturaPlugin extends IntegracioPlugin, SalutPlugin {

	public ValidaSignaturaResposta validaSignatura(
			String documentNom,
			String documentMime,
			byte[] documentContingut,
			byte[] firmaContingut
			) throws SistemaExternException;

}
