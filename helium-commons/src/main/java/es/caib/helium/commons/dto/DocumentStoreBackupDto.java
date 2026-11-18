package es.caib.helium.commons.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentStoreBackupDto extends DocumentStoreDto {
	private String ntiDefinicionGenCsv;
	private String ntiCsv;
	private String arxiuUuid;
	private boolean signat = false;
	private byte[] arxiuContingut;

	private static final long serialVersionUID = 1L;
}
