/**
 * 
 */
package net.conselldemallorca.helium.api.dto;


/**
 * Enumeració amb els possibles estats d'un enviament d'un
 * document a un sistema extern.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum DocumentEnviamentEstatEnumDto {
	ENVIAT,
	PROCESSAT_OK,
	PROCESSAT_REBUTJAT,
	PROCESSAT_ERROR
}
