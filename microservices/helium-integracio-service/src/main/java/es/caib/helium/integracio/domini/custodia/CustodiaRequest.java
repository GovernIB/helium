package es.caib.helium.integracio.domini.custodia;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustodiaRequest {

	@NotNull 
	private String documentId;
	private String gesDocId; //TODO Per la implementacio de Caib no es fa servir.
	@NotNull
	private String nomArxiuSignat;
	@NotNull
	private String codiTipusCustodia;
	@NotNull
	private byte[] signatura;
}
