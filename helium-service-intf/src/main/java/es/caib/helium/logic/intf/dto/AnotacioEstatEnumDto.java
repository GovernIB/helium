/**
 * 
 */
package es.caib.helium.logic.intf.dto;


/**
 * Enumeració amb els estats d'una anotació provinent de Distribució
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum AnotacioEstatEnumDto {
	PENDENT,  	// Anotació pendent de processar o rebutjar. Apareix en el llistat d'anotacions pendents
	PROCESSADA, // Anotació processada i relacionada amb un nou expedient no un expedient existent
	REBUTJADA 	// Anotació rebutjada
}
