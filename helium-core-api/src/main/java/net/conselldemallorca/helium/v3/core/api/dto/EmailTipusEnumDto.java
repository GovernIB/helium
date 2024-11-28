/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;


/**
 * Enumeració amb els tipus d'emails segons com arribi una anotació provinent de Distribució
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum EmailTipusEnumDto {
	REBUDA_PENDENT,// L'anotació ha arribat i està pendent (no es processarà automàticament) --> tampoc té expedient associat??
	PROCESSADA, // L'anotació s'ha processat i s'ha creat un expedient
	INCORPORADA // L'anotació s'ha incorporat a un expedient existent.	
}