package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import net.conselldemallorca.helium.webapp.v3.command.ExpedientAltaMassivaCommand.AltaMassiva;
import net.conselldemallorca.helium.webapp.v3.validator.ExpedientAltaMassiva;

/** Command pel formulari d'alta massiva d'expedients a partir d'una fulla CSV.
 * 
 */
@ExpedientAltaMassiva(groups = {AltaMassiva.class})
public class ExpedientAltaMassivaCommand {

	
	// Informació per l'alta massiva
	@NotNull(groups = {AltaMassiva.class})
	private Long expedientTipusId = null;
	// Arxiu CSV
	private MultipartFile file;
	
	/** Propietat on parsejar el CSV durant la validació en el @ExpedientAltaMassivaValidator */
	private String[][] contingutCsv = null;;
	
	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}
	
	public MultipartFile getFile() {
		return file;
	}
	public void setFile(MultipartFile file) {
		this.file = file;
	}
	
	public interface AltaMassiva {}

	public String[][] getContingutCsv() {
		return contingutCsv;
	}
	public void setContingutCsv(String[][] contingutCsv) {
		this.contingutCsv = contingutCsv;
	}
}
