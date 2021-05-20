/**
 * 
 */
package es.caib.helium.api.dto;

import java.io.Serializable;

/**
 * Enumerat que indica l'estat d'una notificació per a un destinatari.
 * 
 * Els possibles estats son:
 * 	- NOTIB_PENDENT: La notificació està pendent d'enviar (Notifica)
 *  - NOTIB_ENVIADA: La notificació s'ha enviat correctament (Notifica)
 *  - Ausente. (sólo notificaciones)
 *  - Dirección incorrecta. (sólo notificaciones)
 *  - Desconocido. (sólo notificaciones)
 *  - Enviado a la Dirección electrónica habilitada.
 *  - Envío programado: Cuando la comunicación o notificación se encuentra en espera de ser 
 *  			enviada en la fecha indicada por el usuario.
 *  - Entregado al Operador Postal. Aunque este estado actualmente no está en uso, se espera 
 *  			que en el futuro los CIE indiquen que un envío ha sido ya procesado y se ha 
 *  			entregado al Operador Postal para su reparto.
 *  - Error en la entrega. Error en la entrega. Indica que se ha producido un error en el 
 *  			Agente Colaborador (por ejemplo, formato A4 del PDF incorrecto).
 *  			Es un estado final, por lo que no se reintentará el envío. Debe tratarse como
 *  			un error. Se puede investigar qué ha ocurrido observando el detalle del envío 
 *  			a través de la Aplicación Web.
 *  - Expirada: Cuando han pasado los 10 días que establece la Ley para poder comparecer 
 *  			(o el plazo que se haya indicado) y el ciudadano no haya comparecido.
 *  			En la Versión V1 de los servicios web las notificaciones Expiradas aparecen 
 *  			como Rehusadas sin poder hacer distinción.
 *  - Extraviada. (sólo notificaciones)
 *  - Fallecido: cuando el destinatario de la notificación o comunicación ha fallecido. 
 *  			(sólo notificaciones)
 *  - Leída (sólo comunicaciones leídas en Carpeta Ciudadana o Sede Electrónica).
 *  - Notificada. (sólo notificaciones)
 *  - Pendiente de envío. El envío se va a entregar al CIE o la DEH según corresponda (se 
 *  			sustituye en V2 por pendiente CIE o pendiente DEH).
 *  - Pendiente sede: Cuando existe un número de días naturales que estará disponible el envío 
 *  			para su comparecencia desde la sede electrónica del Punto de Acceso General 
 *  			(Carpeta Ciudadana) antes de enviar a otro medio alternativo de entrega.
 *  - Pendiente de envío CIE. El envío se va a entregar al CIE.
 *  - Pendiente de envío DEH. El envío se va a entregar al DEH.
 *  - Rehusada: Cuando la comunicación o notificación es rechazada por el interesado. También 
 *  			se dará este estado cuando después de los intentos de entrega estipulados por 
 *  			ley, no comparece el interesado. (sólo notificaciones)
 *  - Sin información.
 *  - Finalitzada: El estado devuelto por Notific@ es un estado final
 *  - Enviada: La notificación/comunicación ha sido enviada a Notific@
 *  - Registrada: La notificación/comunicación ha sido enviada al Registro
 *  - Processada: La notificación/comunicación se ha marcado como procesada por algun motivo.
 *  - Anulada: Notificación incorrecta o errónea
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NotificacioEnviamentEstatEnumDto implements Serializable {
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
	ENVIAT_SIR
}
