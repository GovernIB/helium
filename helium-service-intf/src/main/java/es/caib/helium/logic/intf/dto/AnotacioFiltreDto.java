/**
 * 
 */
package es.caib.helium.logic.intf.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;


/**
 * Command per al filtre d'anotacions de distribució.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AnotacioFiltreDto {

	private String codiProcediment;
	private String codiAssumpte;
	private String numeroExpedient;
	private String numero;
	private String extracte;
	private Date dataInicial;
	private Date dataFinal;
	private AnotacioEstatEnumDto estat;
	private Long expedientTipusId;
	private Long expedientId;

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

	public AnotacioEstatEnumDto getEstat() {
		return estat;
	}

	public void setEstat(AnotacioEstatEnumDto estat) {
		this.estat = estat;
	}
	public Long getExpedientTipusId() {
		return expedientTipusId;
	}

	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}

	public Long getExpedientId() {
		return this.expedientId;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}
}
