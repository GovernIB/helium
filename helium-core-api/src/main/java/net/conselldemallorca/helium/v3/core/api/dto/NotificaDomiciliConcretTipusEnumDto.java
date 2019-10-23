package net.conselldemallorca.helium.v3.core.api.dto;


import java.io.Serializable;

/**
 * Enumerat que indica el tipus de domicili concret per a un destinatari
 * de Notific@.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NotificaDomiciliConcretTipusEnumDto implements Serializable {
	NACIONAL,
	ESTRANGER,
	APARTAT_CORREUS,
	SENSE_NORMALITZAR
}
