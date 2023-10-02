/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.notificacio;

import java.io.Serializable;

/**
 * Enumerat que indica l'estat d'una notificació per a un destinatari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum EnviamentEstat implements Serializable {
	NOTIB_PENDENT,
	NOTIB_ENVIADA,
	ABSENT,
	ADRESA_INCORRECTA,
	DESCONEGUT,
	ENVIADA_CI,
	ENVIADA_DEH,
	ENVIAMENT_PROGRAMAT,
	ENTREGADA_OP,
	ERROR_ENTREGA,
	EXPIRADA,
	EXTRAVIADA,
	MORT,
	LLEGIDA,
	NOTIFICADA,
	PENDENT,
	PENDENT_ENVIAMENT,
	PENDENT_SEU,
	PENDENT_CIE,
	PENDENT_DEH,
	REBUTJADA,
	SENSE_INFORMACIO,
	FINALITZADA,
	ENVIADA,
	REGISTRADA,
	PROCESSADA,
	ANULADA,
	ENVIAT_SIR,
	ENVIADA_AMB_ERRORS,
	FINALITZADA_AMB_ERRORS
}
