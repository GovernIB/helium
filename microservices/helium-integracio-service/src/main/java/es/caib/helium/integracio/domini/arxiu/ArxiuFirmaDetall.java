package es.caib.helium.integracio.domini.arxiu;

import java.util.Date;

import lombok.Data;

@Data
public class ArxiuFirmaDetall {

	private Date data;
	private String responsableNif;
	private String responsableNom;
	private String emissorCertificat;
}
