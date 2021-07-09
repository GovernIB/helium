package es.caib.helium.back.command;

import es.caib.helium.back.validator.ExpedientAltaMassiva;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.Date;

/** Command pel formulari d'alta massiva d'expedients a partir d'una fulla CSV.
 * 
 */
@ExpedientAltaMassiva(groups = {ExpedientAltaMassivaCommand.AltaMassiva.class})
public class ExpedientAltaMassivaCommand {

	// Informació per programar l'execució massiva
	private String dataInici;
	private boolean correu;
	
	// Informació per l'alta massiva
	@NotNull(groups = {AltaMassiva.class})
	private Long expedientTipusId = null;
	// Arxiu CSV
	private MultipartFile file;
	
	/** Propietat on parsejar el CSV durant la validació en el @ExpedientAltaMassivaValidator */
	private String[][] contingutCsv = null;;
	
	public String getDataInici() {
		return dataInici;
	}
	public void setDataInici(String dataInici) {
		this.dataInici = dataInici;
	}
	public boolean isCorreu() {
		return correu;
	}
	public void setCorreu(boolean correu) {
		this.correu = correu;
	}
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
	
	public Date getDataIniciAsDate() {
		Date dataInici = null;
		if (this.dataInici != null && !this.dataInici.isEmpty())
		{
			try {
				dataInici = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(this.dataInici);
			} catch (Exception e) {
			}
		}
		return dataInici;
	}
}
