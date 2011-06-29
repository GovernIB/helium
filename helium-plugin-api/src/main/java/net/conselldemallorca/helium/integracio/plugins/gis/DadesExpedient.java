/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.gis;

import java.io.Serializable;

/**
 * Classe que representa un expedient en un sistema extern
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesExpedient implements Serializable {

	private String refCatastral;
	private String numero;
	private String titol;
	private String expedientTipusCodi;
	private String expedientTipusNom;
	private String estatCodi;
	private String estatNom;
	private String processInstanceId;
	
	public DadesExpedient(String refCatastral, String numero, String expedientTipusCodi, String expedientTipusNom, String estatCodi, String estatNom, String processInstanceId) {
		this.refCatastral = refCatastral;
		this.numero = numero;
		this.expedientTipusCodi = expedientTipusCodi;
		this.expedientTipusNom = expedientTipusNom;
		this.estatCodi = estatCodi;
		this.estatNom = estatNom;
		this.processInstanceId = processInstanceId;
	}
	public DadesExpedient(String refCatastral, String numero, String titol, String expedientTipusCodi, String expedientTipusNom, String estatCodi, String estatNom, String processInstanceId) {
		this.refCatastral = refCatastral;
		this.numero = numero;
		this.titol = titol;
		this.expedientTipusCodi = expedientTipusCodi;
		this.expedientTipusNom = expedientTipusNom;
		this.estatCodi = estatCodi;
		this.estatNom = estatNom;
		this.processInstanceId = processInstanceId;
	}

	public String getRefCatastral() {
		return refCatastral;
	}
	public void setRefCatastral(String refCatastral) {
		this.refCatastral = refCatastral;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public String getExpedientTipusCodi() {
		return expedientTipusCodi;
	}
	public void setExpedientTipusCodi(String expedientTipusCodi) {
		this.expedientTipusCodi = expedientTipusCodi;
	}
	public String getEstatCodi() {
		return estatCodi;
	}
	public void setEstatCodi(String estatCodi) {
		this.estatCodi = estatCodi;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public String getExpedientTipusNom() {
		return expedientTipusNom;
	}
	public void setExpedientTipusNom(String expedientTipusNom) {
		this.expedientTipusNom = expedientTipusNom;
	}
	public String getEstatNom() {
		return estatNom;
	}
	public void setEstatNom(String estatNom) {
		this.estatNom = estatNom;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((processInstanceId == null) ? 0 : processInstanceId
						.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DadesExpedient other = (DadesExpedient) obj;
		if (processInstanceId == null) {
			if (other.processInstanceId != null)
				return false;
		} else if (!processInstanceId.equals(other.processInstanceId))
			return false;
		return true;
	}

	private static final long serialVersionUID = 1L;
}
