/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.Date;

import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto.ExecucioMassivaTipusDto;

/**
 * DTO amb informació d'una execució massiva
 * d'expedients simplificada per a un llistat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExecucioMassivaListDto {

	private Long id;
	private ExecucioMassivaTipusDto tipus;
	private String usuari;
	private Date dataInici;
	private Date dataFi;
	private Boolean enviarCorreu;
	private Long expedientTipusId;
	private Long finalitzat;
	private Long error;
	private Long processat;
	private Long pendent;
	private Long total;
	
	public Long getFinalitzat() {
		return finalitzat;
	}
	public void setFinalitzat(Long finalitzat) {
		this.finalitzat = finalitzat;
	}
	public Long getError() {
		return error;
	}
	public void setError(Long error) {
		this.error = error;
	}
	public Long getProcessat() {
		return processat;
	}
	public void setProcessat(Long processat) {
		this.processat = processat;
	}
	public Long getPendent() {
		return pendent;
	}
	public void setPendent(Long pendent) {
		this.pendent = pendent;
	}
	public Long getTotal() {
		return total;
	}
	public void setTotal(Long total) {
		this.total = total;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public ExecucioMassivaTipusDto getTipus() {
		return tipus;
	}
	public void setTipus(ExecucioMassivaTipusDto tipus) {
		this.tipus = tipus;
	}
	public String getUsuari() {
		return usuari;
	}
	public void setUsuari(String usuari) {
		this.usuari = usuari;
	}
	public Date getDataInici() {
		return dataInici;
	}
	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}
	public Date getDataFi() {
		return dataFi;
	}
	public void setDataFi(Date dataFi) {
		this.dataFi = dataFi;
	}
	public Boolean getEnviarCorreu() {
		return enviarCorreu;
	}
	public void setEnviarCorreu(Boolean enviarCorreu) {
		this.enviarCorreu = enviarCorreu;
	}
	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}
}

