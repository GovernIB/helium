/**
 * 
 */
package es.caib.helium.back.command;

import es.caib.helium.logic.intf.dto.AnotacioEstatEnumDto;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

/**
 * Command per al filtre d'expedients dels arxius.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AnotacioFiltreCommand {

	private String codiProcediment;
	private String codiAssumpte;
	private String numeroExpedient;
	private String numero;
	private String extracte;
	private Date dataInicial;
	private Date dataFinal;
	private Long expedientTipusId;
	private AnotacioEstatEnumDto estat;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public String getCodiProcediment() {
		return codiProcediment;
	}

	public void setCodiProcediment(String codiProcediment) {
		this.codiProcediment = codiProcediment;
	}

	public String getCodiAssumpte() {
		return codiAssumpte;
	}

	public void setCodiAssumpte(String codiAssumpte) {
		this.codiAssumpte = codiAssumpte;
	}

	public String getNumeroExpedient() {
		return numeroExpedient;
	}

	public void setNumeroExpedient(String numeroExpedient) {
		this.numeroExpedient = numeroExpedient;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getExtracte() {
		return extracte;
	}

	public void setExtracte(String extracte) {
		this.extracte = extracte;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public Long getExpedientTipusId() {
		return expedientTipusId;
	}

	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}

	public AnotacioEstatEnumDto getEstat() {
		return estat;
	}

	public void setEstat(AnotacioEstatEnumDto estat) {
		this.estat = estat;
	}
	
}
