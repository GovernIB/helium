/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.Date;


/**
 * DTO amb informació d'una mesura temporal per a revisar
 * el rendiment de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TascaCompleteDto {

	String tascaId;
	Long expedientId;
	String tipusExpedient;
	String expedient;
	String tasca;
	Date inici;
	Double tempsExecucio;
	
	public String getTascaId() {
		return tascaId;
	}
	public void setTascaId(String tascaId) {
		this.tascaId = tascaId;
	}
	public Long getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}
	public String getTipusExpedient() {
		return tipusExpedient;
	}
	public void setTipusExpedient(String tipusExpedient) {
		this.tipusExpedient = tipusExpedient;
	}
	public String getExpedient() {
		return expedient;
	}
	public void setExpedient(String expedient) {
		this.expedient = expedient;
	}
	public String getTasca() {
		return tasca;
	}
	public void setTasca(String tasca) {
		this.tasca = tasca;
	}
	public Date getInici() {
		return inici;
	}
	public void setInici(Date inici) {
		this.inici = inici;
	}
	public Double getTempsExecucio() {
		return tempsExecucio;
	}
	public void setTempsExecucio(Double tempsExecucio) {
		this.tempsExecucio = tempsExecucio;
	}
}
