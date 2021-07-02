package es.caib.helium.logic.intf.dto;

/**
 * Enumerat que indica l'estat d'una notificació a dins NotIB.
 * 
 * Els possibles estats son:
 *  - PENDENT: Pendent d'enviament a Notifica.
 *  - ENVIADA: Enviada a Notifica.
 *  - REGISTRADA: Enviada al registre.
 *  - FINALITZADA: Estat final de la notificació.
 * 
 */
public enum NotificacioEstatEnumDto {
	PENDENT,
	ENVIADA,
	REGISTRADA,
	FINALITZADA,
	PROCESSADA
}
