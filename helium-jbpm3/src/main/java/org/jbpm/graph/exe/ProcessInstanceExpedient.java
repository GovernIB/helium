/**
 *
 */
package org.jbpm.graph.exe;

import java.io.Serializable;
import java.util.Date;

import org.jbpm.graph.def.Identifiable;

/**
 * Objecte de domini que representa un expedient de la instància de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ProcessInstanceExpedient implements Identifiable, Serializable {

	private long id;
	private String titol;
	private String numero;
	private String numeroDefault;
	private Date dataInici;
	private Date dataFi;
	private long expedientTipusId;
	private long entornId;
	private String processInstanceId;
	private boolean ambRetroaccio;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
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
	public String getNumeroDefault() {
		return numeroDefault;
	}
	public void setNumeroDefault(String numeroDefault) {
		this.numeroDefault = numeroDefault;
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
	public long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}
	public long getEntornId() {
		return entornId;
	}
	public void setEntornId(long entornId) {
		this.entornId = entornId;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public boolean isAmbRetroaccio() {
		return ambRetroaccio;
	}
	public void setAmbRetroaccio(boolean ambRetroaccio) {
		this.ambRetroaccio = ambRetroaccio;
	}

	private static final long serialVersionUID = 1L;

}
