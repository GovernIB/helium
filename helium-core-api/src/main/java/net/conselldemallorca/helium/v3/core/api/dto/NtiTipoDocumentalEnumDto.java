/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import net.conselldemallorca.helium.v3.core.api.registre.RegistreAnnexNtiTipusDocumentEnum;

/**
 * Possibles tipus de document segons NTI.
 * 
 * RESOLUCIO: Resolución
 * ACORD: Acuerdo
 * CONTRACTE: Contrato
 * CONVENI: Convenio
 * DECLARACIO: Declaración
 * COMUNICACIO: Comunicación
 * NOTIFICACIO: Notificación
 * PUBLICACIO: Publicación
 * JUSTIFICANT_RECEPCIO: Acuse de recibo
 * ACTA: Acta
 * CERTIFICAT: Certificat 
 * DILIGENCIA: Diligència
 * INFORME: Informe
 * SOLICITUD: Solicitud
 * DENUNCIA: Denuncia
 * ALEGACIO: Alegación
 * RECURS: Recurso
 * COMUNICACIO_CIUTADA: Comunicación ciudadano 
 * FACTURA: Factura
 * ALTRES_INCAUTATS: Otros incautados 
 * ALTRES: Otros
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NtiTipoDocumentalEnumDto {
	RESOLUCIO,
	ACORD,
	CONTRACTE,
	CONVENI,
	DECLARACIO,
	COMUNICACIO,
	NOTIFICACIO,
	PUBLICACIO,
	JUSTIFICANT_RECEPCIO,
	ACTA,
	CERTIFICAT,
	DILIGENCIA,
	INFORME,
	SOLICITUD,
	DENUNCIA,
	ALEGACIO,
	RECURS,
	COMUNICACIO_CIUTADA,
	FACTURA,
	ALTRES_INCAUTATS,
	LLEI,
	MOCIO,
	INSTRUCCIO,
	CONVOCATORIA,
	ORDRE_DIA,
	INFORME_PONENCIA,
	DICTAMEN_COMISSIO,
	INICIATIVA_LEGISLATIVA,
	PREGUNTA,
	INTERPELACIO,
	RESPOSTA,
	PROPOSICIO_NO_LLEI,
	ESMENA,
	PROPOSTA_RESOLUCIO,
	COMPAREIXENSA,
	SOLICITUD_INFORMACIO,
	ESCRIT,
	INICIATIVA__LEGISLATIVA,
	PETICIO,
	ALTRES;
	
	/** Retorna el codi NTI pel tipus de document. */
	public String getValorNti() {
		return RegistreAnnexNtiTipusDocumentEnum.valueOf(this.name()).getValor();
	}
	/** Retorna el valor numèric del NTI pel tipus de document. */
	public Integer getValorNumericNti() {
		return Integer.valueOf(getValorNti().substring(2));
	}
}
