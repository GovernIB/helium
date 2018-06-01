/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

/**
 * Possibles orígens d'un document segons NTI.
 * 
 * Possibles tipus de firma.
 * 
 * CSV: Código seguro de verificación
 * XADES_DET: XAdES internally detached signature
 * XADES_ENV: XAdES enveloped signature
 * CADES_DET: CAdES detached/explicit signature
 * CADES_ATT: CAdES attached/implicit signature
 * PADES: PAdES
 * SMIME
 * ODT
 * OOXML
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NtiTipoFirmaEnumDto {
	CSV,
	XADES_DET,
	XADES_ENV,
	CADES_DET,
	CADES_ATT,
	PADES,
	SMIME,
	ODT,
	OOXML;
}
