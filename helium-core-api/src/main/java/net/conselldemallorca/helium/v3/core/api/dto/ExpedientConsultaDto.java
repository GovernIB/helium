/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * DTO amb informació del filtre de consulta d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientConsultaDto {

	private String titol;
	private String numero;
	private Long expedientTipusId;
	private Long estatId;
	private Date dataIniciInicial;
	private Date dataIniciFinal;



	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}
	public Long getEstatId() {
		return estatId;
	}
	public void setEstatId(Long estatId) {
		this.estatId = estatId;
	}
	public Date getDataIniciInicial() {
		return dataIniciInicial;
	}
	public void setDataIniciInicial(Date dataIniciInicial) {
		this.dataIniciInicial = dataIniciInicial;
	}
	public Date getDataIniciFinal() {
		return dataIniciFinal;
	}
	public void setDataIniciFinal(Date dataIniciFinal) {
		this.dataIniciFinal = dataIniciFinal;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
