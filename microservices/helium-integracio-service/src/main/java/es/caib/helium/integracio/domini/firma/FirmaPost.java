package es.caib.helium.integracio.domini.firma;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import es.caib.helium.integracio.enums.firma.FirmaTipus;
import lombok.Data;

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
}
