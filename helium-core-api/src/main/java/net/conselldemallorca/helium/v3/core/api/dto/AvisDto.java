package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * InformaciÃ³ d'una avis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AvisDto {

	private Long id;
	private String assumpte;
	private String missatge;
	private Date dataInici;
	private Date dataFinal;
	private String horaInici;
	private String horaFi;
	private Boolean actiu;
	private AvisNivellEnumDto avisNivell;

	
	public String getAssumpte() {
		return assumpte;
	}
	public void setAssumpte(String assumpte) {
		this.assumpte = assumpte;
	}
	public String getMissatge() {
		return missatge;
	}
	public void setMissatge(String missatge) {
		this.missatge = missatge;
	}
	public Date getDataInici() {
		return dataInici;
	}
	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}
	public Date getDataFinal() {
		return dataFinal;
	}
	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}
	public String getHoraInici() {
		return horaInici;
	}
	public void setHoraInici(String horaInici) {
		this.horaInici = horaInici;
	}
	public String getHoraFi() {
		return horaFi;
	}
	public void setHoraFi(String horaFi) {
		this.horaFi = horaFi;
	}
	public Boolean getActiu() {
		return actiu;
	}
	public void setActiu(Boolean actiu) {
		this.actiu = actiu;
	}
	public AvisNivellEnumDto getAvisNivell() {
		return avisNivell;
	}
	public void setAvisNivell(AvisNivellEnumDto avisNivell) {
		this.avisNivell = avisNivell;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
}
