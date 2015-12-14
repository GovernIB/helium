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
		INFORME
	}

	private Long id;
	private String campCodi;
	private String defprocJbpmKey;
	private int defprocVersio = -1;
	private TipusConsultaCamp tipus;
	private int ordre;

	public ConsultaCampDto() {}
	public ConsultaCampDto(String campCodi, TipusConsultaCamp tipus) {
		this.campCodi = campCodi;
		this.tipus = tipus;
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

	private static final long serialVersionUID = 1L;
}
