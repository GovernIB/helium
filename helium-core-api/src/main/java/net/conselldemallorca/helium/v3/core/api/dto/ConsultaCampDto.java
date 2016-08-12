/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;

/**
 * Objecte de domini que representa una camp d'una consulta d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ConsultaCampDto implements Serializable {

	public enum TipusConsultaCamp {
		FILTRE,
		INFORME,
		PARAM
	}
	public enum TipusParamConsultaCamp {
		TEXT,
		SENCER,
		FLOTANT,
		DATA,
		BOOLEAN
	}
	
	private Long id;
	private Long campId;
	private String campCodi;
	private String campDescripcio;
	private String defprocJbpmKey;
	private int defprocVersio = -1;
	private TipusConsultaCamp tipus;
	private int ordre;
	private TipusParamConsultaCamp paramTipus;

	private CampTipusDto campTipus;

	public ConsultaCampDto() {}
	public ConsultaCampDto(String campCodi, TipusConsultaCamp tipus) {
		this.campCodi = campCodi;
		this.tipus = tipus;
	}

	public CampTipusDto getCampTipus() {
		return campTipus;
	}
	public void setCampTipus(CampTipusDto campTipus) {
		this.campTipus = campTipus;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getCampId() {
		return campId;
	}
	public void setCampId(Long campId) {
		this.campId = campId;
	}
	public String getCampCodi() {
		return campCodi;
	}
	public void setCampCodi(String campCodi) {
		this.campCodi = campCodi;
	}
	public String getCampDescripcio() {
		return campDescripcio;
	}
	public void setCampDescripcio(String campDescripcio) {
		this.campDescripcio = campDescripcio;
	}
	public String getDefprocJbpmKey() {
		return defprocJbpmKey;
	}
	public void setDefprocJbpmKey(String defprocJbpmKey) {
		this.defprocJbpmKey = defprocJbpmKey;
	}
	public int getDefprocVersio() {
		return defprocVersio;
	}
	public void setDefprocVersio(int defprocVersio) {
		this.defprocVersio = defprocVersio;
	}
	public TipusConsultaCamp getTipus() {
		return tipus;
	}
	public void setTipus(TipusConsultaCamp tipus) {
		this.tipus = tipus;
	}
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}

	public TipusParamConsultaCamp getParamTipus() {
		return paramTipus;
	}
	public void setParamTipus(TipusParamConsultaCamp paramTipus) {
		this.paramTipus = paramTipus;
	}

	private static final long serialVersionUID = 1L;
}
