/**
 * 
 */
package es.caib.helium.logic.intf.integracio.notificacio;

import java.io.Serializable;

/**
 * Enumerat que indica el tipus de domicili per a un destinatari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum EntregaPostalTipus implements Serializable {
	NACIONAL,
	ESTRANGER,
	APARTAT_CORREUS,
	SENSE_NORMALITZAR
}
