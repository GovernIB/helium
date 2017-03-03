/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.Date;
import java.util.List;

/**
 * DTO amb informació d'una execució massiva
 * d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExecucioMassivaDto {

	public enum ExecucioMassivaTipusDto {
		EXECUTAR_TASCA,
		ACTUALITZAR_VERSIO_DEFPROC,
		ELIMINAR_VERSIO_DEFPROC,
		EXECUTAR_SCRIPT,
		EXECUTAR_ACCIO,
		ATURAR_EXPEDIENT,
		MODIFICAR_VARIABLE,
		MODIFICAR_DOCUMENT,
		REINDEXAR,
		REASSIGNAR,
		BUIDARLOG,
		REPRENDRE_EXPEDIENT,
		REPRENDRE,
		PROPAGAR_PLANTILLES,
		PROPAGAR_CONSULTES,
		FINALITZAR_EXPEDIENT
	}

	private Long id;
	private ExecucioMassivaTipusDto tipus;
	private String usuari;
	private Date dataInici;
	private Date dataFi;
	private Boolean enviarCorreu;
	private String param1;
	private byte[] param2;

	private Long expedientTipusId;

	private List<Long> expedientIds;
	private String[] tascaIds;
	private List<String> procInstIds;
	private Long[] defProcIds;

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
	public List<Long> getExpedientIds() {
		return expedientIds;
	}
	public void setExpedientIds(List<Long> expedientIds) {
		this.expedientIds = expedientIds;
	}
	public String[] getTascaIds() {
		return tascaIds;
	}
	public void setTascaIds(String[] tascaIds) {
		this.tascaIds = tascaIds;
	}
	public List<String> getProcInstIds() {
		return procInstIds;
	}
	public void setProcInstIds(List<String> procInstIds) {
		this.procInstIds = procInstIds;
	}
	public Long[] getDefProcIds() {
		return defProcIds;
	}
	public void setDefProcIds(Long[] defProcIds) {
		this.defProcIds = defProcIds;
	}
}
