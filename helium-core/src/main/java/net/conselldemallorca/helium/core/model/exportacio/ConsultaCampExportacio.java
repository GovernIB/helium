/**
 * 
 */
package net.conselldemallorca.helium.core.model.exportacio;

import java.io.Serializable;

import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusParamConsultaCamp;

/**
 * DTO amb informació d'un camp d'una consulta per exportar
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ConsultaCampExportacio implements Serializable {
	private String codi;
	private String jbpmKey;
	private TipusConsultaCamp tipusConsultaCamp;
	private TipusParamConsultaCamp tipusParamConsultaCamp;
	private TipusCamp tipusCamp;
	private String campDescripcio;
	private int ordre;

	public ConsultaCampExportacio(
			String codi,
			String jbpmKey,
			TipusConsultaCamp tipusConsultaCamp,
			TipusParamConsultaCamp tipusParamConsultaCamp,
			TipusCamp tipusCamp,
			String campDescripcio,
			int ordre) {
		this.codi = codi;
		this.jbpmKey = jbpmKey;
		this.tipusConsultaCamp = tipusConsultaCamp;
		this.tipusParamConsultaCamp = tipusParamConsultaCamp;
		this.tipusCamp = tipusCamp;
		this.campDescripcio = campDescripcio;
		this.ordre = ordre;
	}

	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getJbpmKey() {
		return jbpmKey;
	}
	public void setJbpmKey(String jbpmKey) {
		this.jbpmKey = jbpmKey;
	}
	public TipusConsultaCamp getTipusConsultaCamp() {
		return tipusConsultaCamp;
	}
	public void setTipusConsultaCamp(TipusConsultaCamp tipusConsultaCamp) {
		this.tipusConsultaCamp = tipusConsultaCamp;
	}
	public TipusCamp getTipusCamp() {
		return tipusCamp;
	}
	public void setTipusCamp(TipusCamp tipusCamp) {
		this.tipusCamp = tipusCamp;
	}
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}
	public TipusParamConsultaCamp getTipusParamConsultaCamp() {
		return tipusParamConsultaCamp;
	}
	public void setTipusParamConsultaCamp(TipusParamConsultaCamp tipusParamConsultaCamp) {
		this.tipusParamConsultaCamp = tipusParamConsultaCamp;
	}	
	public String getCampDescripcio() {
		return campDescripcio;
	}
	public void setCampDescripcio(String campDescripcio) {
		this.campDescripcio = campDescripcio;
	}

	private static final long serialVersionUID = 1L;
}
