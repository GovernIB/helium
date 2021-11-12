package es.caib.helium.client.integracio.firma.model;

import lombok.Data;

@Data
public class FirmaPost {
	
	private String id;
	private String nom;
	private String motiu;
	private byte[] contingut;
	private String mime;
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
