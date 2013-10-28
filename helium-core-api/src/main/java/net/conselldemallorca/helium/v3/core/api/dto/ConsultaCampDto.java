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
	private ConsultaDto consulta;

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
	public ConsultaDto getConsulta() {
		return consulta;
	}
	public void setConsulta(ConsultaDto consulta) {
		this.consulta = consulta;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((campCodi == null) ? 0 : campCodi.hashCode());
		result = prime * result
				+ ((consulta == null) ? 0 : consulta.hashCode());
		result = prime * result
				+ ((defprocJbpmKey == null) ? 0 : defprocJbpmKey.hashCode());
		result = prime * result + defprocVersio;
		result = prime * result + ((tipus == null) ? 0 : tipus.hashCode());
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
		ConsultaCampDto other = (ConsultaCampDto) obj;
		if (campCodi == null) {
			if (other.campCodi != null)
				return false;
		} else if (!campCodi.equals(other.campCodi))
			return false;
		if (consulta == null) {
			if (other.consulta != null)
				return false;
		} else if (!consulta.equals(other.consulta))
			return false;
		if (defprocJbpmKey == null) {
			if (other.defprocJbpmKey != null)
				return false;
		} else if (!defprocJbpmKey.equals(other.defprocJbpmKey))
			return false;
		if (defprocVersio != other.defprocVersio)
			return false;
		if (tipus == null) {
			if (other.tipus != null)
				return false;
		} else if (!tipus.equals(other.tipus))
			return false;
		return true;
	}

	private static final long serialVersionUID = 1L;

}
