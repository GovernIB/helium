package es.caib.helium.client.integracio.firma.model;

import es.caib.helium.client.integracio.firma.enums.FirmaTipus;
import lombok.Data;

@Data
public class FirmaPost {
	
	private FirmaTipus firmaTipus;
	private String motiu;
	private String arxiuNom;
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
