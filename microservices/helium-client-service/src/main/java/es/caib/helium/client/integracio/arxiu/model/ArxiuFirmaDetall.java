package es.caib.helium.client.integracio.arxiu.model;

import java.util.Date;

import lombok.Data;

@Data
public class ArxiuFirmaDetall {

	private Date data;
	private String responsableNif;
	private String responsableNom;
	private String emissorCertificat;
}
