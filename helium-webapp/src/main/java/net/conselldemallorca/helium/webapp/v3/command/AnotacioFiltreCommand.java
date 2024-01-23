/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import net.conselldemallorca.helium.v3.core.api.dto.AnotacioEstatEnumDto;

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
	private String anotacioTramitacioMassivaAnotacioId;
	private String unitatOrganitzativaCodi;

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

	public String getAnotacioTramitacioMassivaAnotacioId() {
		return anotacioTramitacioMassivaAnotacioId;
	}

	public void setAnotacioTramitacioMassivaAnotacioId(String anotacioTramitacioMassivaAnotacioId) {
		this.anotacioTramitacioMassivaAnotacioId = anotacioTramitacioMassivaAnotacioId;
	}

	public String getUnitatOrganitzativaCodi() {
		return unitatOrganitzativaCodi;
	}

	public void setUnitatOrganitzativaCodi(String unitatOrganitzativaCodi) {
		this.unitatOrganitzativaCodi = unitatOrganitzativaCodi;
	}


	
}
