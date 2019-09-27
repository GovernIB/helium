/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Command per al filtre d'anotacions de distribució.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AnotacioFiltreDto {

	private String procediment;
	private String codiAssumpte;
	private String numeroExpedient;
	private String numero;
	private String extracte;
	private String origen;
	private Date dataInicial;
	private Date dataFinal;
	private AnotacioEstatEnumDto estat;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public String getProcediment() {
		return procediment;
	}

	public void setProcediment(String procediment) {
		this.procediment = procediment;
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

	public String getOrigen() {
		return origen;
	}

	public void setOrigen(String origen) {
		this.origen = origen;
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
}
