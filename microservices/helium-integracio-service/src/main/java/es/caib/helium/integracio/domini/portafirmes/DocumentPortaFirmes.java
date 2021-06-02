package es.caib.helium.integracio.domini.portafirmes;

import lombok.Data;

@Data
public class DocumentPortaFirmes {
	
	private String titol;
	private String arxiuNom;
	private byte[] arxiuContingut;
	private Integer tipus;
	private boolean isSignat;
	private String reference;
	private String descripcio;
}
