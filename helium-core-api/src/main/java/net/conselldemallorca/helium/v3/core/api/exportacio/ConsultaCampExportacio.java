/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exportacio;

import java.io.Serializable;

import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaCampDto.TipusConsultaCamp;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaCampDto.TipusParamConsultaCamp;

/**
 * DTO amb informació d'un camp d'una consulta per exportar
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ConsultaCampExportacio implements Serializable {

	private String campCodi;
	private String jbpmKey;
	private TipusConsultaCamp tipusConsultaCamp;
	private TipusParamConsultaCamp tipusParamConsultaCamp;
	private CampTipusDto tipusCamp;
	private String campDescripcio;
	private int ordre;

	public ConsultaCampExportacio(
			String campCodi,
			String jbpmKey,
			TipusConsultaCamp tipusConsultaCamp,
			TipusParamConsultaCamp tipusParamConsultaCamp,
			CampTipusDto tipusCamp,
			String campDescripcio,
			int ordre) {
		this.campCodi = campCodi;
		this.jbpmKey = jbpmKey;
		this.tipusConsultaCamp = tipusConsultaCamp;
		this.tipusParamConsultaCamp = tipusParamConsultaCamp;
		this.tipusCamp = tipusCamp;
		this.campDescripcio = campDescripcio;
		this.ordre = ordre;
	}

	public String getCampCodi() {
		return campCodi;
	}
	public void setCampCodi(String codi) {
		this.campCodi = codi;
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
	public CampTipusDto getCampTipusDto() {
		return tipusCamp;
	}
	public void setCampTipusDto(CampTipusDto tipusCamp) {
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
