/**
 * 
 */
package es.caib.helium.logic.intf.dto;

import es.caib.helium.logic.intf.util.ExpedientCamps;

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
	private String campCodi;
	private String campEtiqueta;
	private String campDescripcio;
	private String defprocJbpmKey;
	private int defprocVersio = -1;
	private TipusConsultaCamp tipus;
	private int ordre;
	private int ampleCols;
	private int buitCols;
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
	public String getCampCodi() {
		return campCodi;
	}
	public void setCampCodi(String campCodi) {
		this.campCodi = campCodi;
	}
	public String getCampEtiqueta() {
		return this.campEtiqueta;
	}
	public void setCampEtiqueta(String campEtiqueta) {
		this.campEtiqueta = campEtiqueta;
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
	/** Retorna la combinacio "codi / etiqueta" si no comença amb el prefix de l'expedient. */
	public String getCodiEtiqueta() {
		if (campCodi.startsWith(ExpedientCamps.EXPEDIENT_PREFIX))
			return campEtiqueta != null? campEtiqueta : campCodi;
		else
			return campCodi + (campEtiqueta != null? " / " + campEtiqueta : "");		
	}

	public TipusParamConsultaCamp getParamTipus() {
		return paramTipus;
	}
	public void setParamTipus(TipusParamConsultaCamp paramTipus) {
		this.paramTipus = paramTipus;
	}
	private static final long serialVersionUID = 1L;
}
