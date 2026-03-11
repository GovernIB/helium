package es.caib.helium.commons.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentStoreBackupDto extends DocumentStoreDto {
	private String referenciaFont;
	private String referenciaCustodia;
	private String ntiDefinicionGenCsv;
	private String ntiCsv;
	private String arxiuUuid;
	private boolean signat = false;
	private byte[] arxiuContingut;
}
