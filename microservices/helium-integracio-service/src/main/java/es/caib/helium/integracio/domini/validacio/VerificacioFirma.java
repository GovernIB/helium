package es.caib.helium.integracio.domini.validacio;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class VerificacioFirma {

	@NotNull
	private Long entornId;
	@NotNull
	private Long documentStoreId;
	
	private byte[] documentContingut;
	private byte[] firmaContingut;
	private boolean obtenirDadesCertificat;
	private String contentType;
}
