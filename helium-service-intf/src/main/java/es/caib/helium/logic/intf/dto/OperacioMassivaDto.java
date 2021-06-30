/**
 * 
 */
package es.caib.helium.logic.intf.dto;

import java.util.Date;

import es.caib.helium.logic.intf.dto.ExecucioMassivaDto.ExecucioMassivaTipusDto;

/**
 * DTO amb informació d'una operació massiva 
 * d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class OperacioMassivaDto {

	public enum ExecucioMassivaEstatDto {
		ESTAT_FINALITZAT,
		ESTAT_ERROR,
		ESTAT_PENDENT,
		ESTAT_CANCELAT
	}
	
	private Long id;
	private ExecucioMassivaTipusDto tipus;
	private ExecucioMassivaEstatDto estat;
	private Date dataInici;
	private Date dataFi;
	private Boolean ultimaOperacio;
	private Boolean enviarCorreu;
	private String usuari;
	private String param1;
	private byte[] param2;
	private Long expedientTipusId;
	private Long execucioMassivaId;
	private String error;
	private Integer ordre;
	private ExpedientDto expedient;
	private String tascaId;
	private String processInstanceId;
	
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
	public ExecucioMassivaEstatDto getEstat() {
		return estat;
	}
	public void setEstat(ExecucioMassivaEstatDto estat) {
		this.estat = estat;
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
	public Boolean getUltimaOperacio() {
		return ultimaOperacio;
	}
	public void setUltimaOperacio(Boolean ultimaOperacio) {
		this.ultimaOperacio = ultimaOperacio;
	}
	public Boolean getEnviarCorreu() {
		return enviarCorreu;
	}
	public void setEnviarCorreu(Boolean enviarCorreu) {
		this.enviarCorreu = enviarCorreu;
	}
	public String getUsuari() {
		return usuari;
	}
	public void setUsuari(String usuari) {
		this.usuari = usuari;
	}
	public String getParam1() {
		return param1;
	}
	public void setParam1(String param1) {
		this.param1 = param1;
	}
	public byte[] getParam2() {
		return param2;
	}
	public void setParam2(byte[] param2) {
		this.param2 = param2;
	}
	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}
	public Long getExecucioMassivaId() {
		return execucioMassivaId;
	}
	public void setExecucioMassivaId(Long execucioMassivaId) {
		this.execucioMassivaId = execucioMassivaId;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public Integer getOrdre() {
		return ordre;
	}
	public void setOrdre(Integer ordre) {
		this.ordre = ordre;
	}
	public ExpedientDto getExpedient() {
		return expedient;
	}
	public void setExpedient(ExpedientDto expedient) {
		this.expedient = expedient;
	}
	public String getTascaId() {
		return tascaId;
	}
	public void setTascaId(String tascaId) {
		this.tascaId = tascaId;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
}
