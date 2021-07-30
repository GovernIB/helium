package es.caib.helium.integracio.domini.firma;

import es.caib.helium.integracio.enums.firma.FirmaTipus;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class FirmaPost {
	
	@NotNull
	private FirmaTipus firmaTipus;
	@NotNull @NotEmpty
	private String motiu;
	@NotNull @NotEmpty
	private String arxiuNom;
	@NotNull @NotEmpty
	private byte[] arxiuContingut;

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
