/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

/**
 * Enumeració amb els diferents tipus d'accions que es poden definir.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum AccioTipusEnumDto  {
	/** Acció definida al flux identificada pel nom i anteriorment configurada com a handler */
	ACCIO,
	/** Acció per invocar un handler propi del flux identificat per la classe que el defineix */
	HANDLER_PROPI,
	/** Acció per invocar un handler propi d'Helium */
	HANDLER_PREDEFINIT,
	/** Acció per entrar un script propi a executar com acció. */
	SCRIPT
}
