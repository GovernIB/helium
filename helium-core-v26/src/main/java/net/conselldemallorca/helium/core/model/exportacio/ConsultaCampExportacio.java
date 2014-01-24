/**
 * 
 */
package net.conselldemallorca.helium.core.model.exportacio;

import java.io.Serializable;

import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusConsultaCamp;



/**
 * DTO amb informació d'un camp d'una consulta per exportar
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ConsultaCampExportacio implements Serializable {

	private String codi;
	private String jbpmKey;
	private TipusConsultaCamp tipusConsultaCamp;
	private TipusCamp tipusCamp;
	private int ordre;



	public ConsultaCampExportacio(
			String codi,
			String jbpmKey,
			TipusConsultaCamp tipusConsultaCamp,
			TipusCamp tipusCamp,
			int ordre) {
		this.codi = codi;
		this.jbpmKey = jbpmKey;
		this.tipusConsultaCamp = tipusConsultaCamp;
		this.tipusCamp = tipusCamp;
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



	private static final long serialVersionUID = 1L;

}
