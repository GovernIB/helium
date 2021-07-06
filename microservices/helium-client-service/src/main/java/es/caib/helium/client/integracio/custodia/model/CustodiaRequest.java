package es.caib.helium.client.integracio.custodia.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustodiaRequest {

	private String documentId;
	private String gesDocId;
	private String nomArxiuSignat;
	private String codiTipusCustodia;
	private byte[] signatura;
}
