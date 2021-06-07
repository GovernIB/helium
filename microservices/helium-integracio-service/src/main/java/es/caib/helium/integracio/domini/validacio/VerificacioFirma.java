package es.caib.helium.integracio.domini.validacio;

import lombok.Data;

@Data
public class VerificacioFirma {

	private byte[] documentContingut;
	private byte[] firmaContingut;
	private boolean obtenirDadesCertificat;
	private String contentType;
}
