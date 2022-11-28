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
	/** Acció que apunta a un handler o acció definida en la definició de procés */
	HANDLER,
	/** Acció per invocar un handler propi d'Helium */
	HANDLER_PREDEFINIT,
	/** Acció per entrar un script propi a executar com acció. */
	SCRIPT
}
