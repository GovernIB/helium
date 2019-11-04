package net.conselldemallorca.helium.v3.core.api.dto;

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
