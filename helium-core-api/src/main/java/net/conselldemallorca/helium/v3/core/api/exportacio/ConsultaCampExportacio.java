/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exportacio;

import java.io.Serializable;

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
	private String campDescripcio;
	private int ordre;
	private int ampleCols;
	private int buitCols;

	public ConsultaCampExportacio(
			String campCodi,
			String jbpmKey,
			TipusConsultaCamp tipusConsultaCamp,
			TipusParamConsultaCamp tipusParamConsultaCamp,
			String campDescripcio,
			int ordre, 
			int ampleCols,
			int buitCols) {
		this.campCodi = campCodi;
		this.jbpmKey = jbpmKey;
		this.tipusConsultaCamp = tipusConsultaCamp;
		this.tipusParamConsultaCamp = tipusParamConsultaCamp;
		this.campDescripcio = campDescripcio;
		this.ordre = ordre;
		this.ampleCols = ampleCols;
		this.buitCols = buitCols;
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
	public int getAmpleCols() {
		return ampleCols;
	}
	public void setAmpleCols(int ampleCols) {
		this.ampleCols = ampleCols;
	}
	public int getBuitCols() {
		return buitCols;
	}
	public void setBuitCols(int buitCols) {
		this.buitCols = buitCols;
	}

	private static final long serialVersionUID = 1L;
}
