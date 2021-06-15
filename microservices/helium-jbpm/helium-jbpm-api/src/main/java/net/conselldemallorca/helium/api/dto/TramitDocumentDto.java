/**
 * 
 */
package net.conselldemallorca.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO amb informació d'un document d'un tràmit.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class TramitDocumentDto {

	protected String nom;
	protected String identificador;
	protected int instanciaNumero;

	protected boolean telematic;
	protected String telematicArxiuNom;
	protected String telematicArxiuExtensio;
	protected byte[] telematicArxiuContingut;
	protected String telematicReferenciaGestorDocumental;
	protected List<TramitDocumentSignaturaDto> telematicSignatures;
	protected long telematicReferenciaCodi;
	protected String telematicReferenciaClau;
	protected Boolean telematicEstructurat;

	protected boolean presencial;
	protected String presencialTipus;
	protected String presencialDocumentCompulsar;
	protected String presencialFotocopia;
	protected String presencialSignatura;

	public TramitDocumentSignaturaDto newDocumentSignatura(
			byte[] signatura,
			String format) {
		TramitDocumentSignaturaDto dto = new TramitDocumentSignaturaDto();
		dto.setSignatura(signatura);
		dto.setFormat(format);
		return dto;
	}

	@Getter @Setter
	public class TramitDocumentSignaturaDto {

		protected byte[] signatura;
		protected String format;

	}

}
