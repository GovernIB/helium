package es.caib.helium.client.integracio.firma.model;

import es.caib.helium.client.integracio.firma.enums.FirmaTipus;
import lombok.Data;

@Data
public class FirmaPost {
	
	private FirmaTipus firmaTipus;
	private String motiu;
	private String arxiuNom;
	private byte[] arxiuContingut;
}
