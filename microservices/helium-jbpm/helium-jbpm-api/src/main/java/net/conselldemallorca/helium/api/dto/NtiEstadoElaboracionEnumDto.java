/**
 * 
 */
package net.conselldemallorca.helium.api.dto;

/**
 * Possibles estats d'elabotació d'un document segons NTI.
 * 
 * ORIGINAL: Document original.
 * COPIA_CF: Còpia electrònica autèntica amb canvi de format.
 * COPIA_DP: Còpia electrònica autèntica de document en paper.
 * COPIA_PR: Còpia electrònica autèntica parcial.
 * ALTRES: Altres.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NtiEstadoElaboracionEnumDto {
	ORIGINAL,
	COPIA_CF,
	COPIA_DP,
	COPIA_PR,
	ALTRES;
}
