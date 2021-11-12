package es.caib.helium.integracio.domini.firma;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class FirmaPost {

	@NotNull @NotEmpty
	private String id;
	@NotNull @NotEmpty
	private String nom;
	@NotNull @NotEmpty
	private String motiu;
	@NotNull
	private byte[] contingut;
	@NotNull @NotEmpty
	private String mime;
	@NotNull @NotEmpty
	private String tipusDocumental;

	
	// Parametres Monitor Integracions
	private Long tamany;
	private Long entornId;
	private String expedientIdentificador;
	private String expedientNumero;
	private Long expedientTipusId;
	private String expedientTipusCodi;
	private String expedientTipusNom;
	private String codiDocument;
}
