/**
 * 
 */
package es.caib.helium.persist.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

/**
 * Objecte de domini que representa una camp d'una consulta d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="hel_consulta_camp",
		uniqueConstraints={@UniqueConstraint(columnNames={"consulta_id", "camp_codi", "defproc_jbpmkey", "defproc_versio", "tipus"})})
@org.hibernate.annotations.Table(
		appliesTo = "hel_consulta_camp",
		indexes = @Index(name = "hel_consultacamp_consulta_i", columnNames = {"consulta_id"}))
public class ConsultaCamp implements Serializable, GenericEntity<Long> {

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
	@NotBlank
	@MaxLength(64)
	private String campCodi;
	@MaxLength(64)
	private String campDescripcio;
	@MaxLength(255)
	private String defprocJbpmKey;
	private int defprocVersio = -1;
	@NotNull
	private TipusConsultaCamp tipus;
	private TipusParamConsultaCamp paramTipus;
	private int ordre;
	private int ampleCols;
	private int buitCols;

	@NotNull
	private Consulta consulta;

	public ConsultaCamp() {}
	public ConsultaCamp(String campCodi, TipusConsultaCamp tipus) {
		this.campCodi = campCodi;
		this.tipus = tipus;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_consulta")
	@TableGenerator(name="gen_consulta", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="camp_codi", length=64, nullable=false)
	public String getCampCodi() {
		return campCodi;
	}
	public void setCampCodi(String campCodi) {
		this.campCodi = campCodi;
	}

	@Column(name="camp_descripcio", length=255, nullable=true)
	public String getCampDescripcio() {
		return campDescripcio;
	}
	public void setCampDescripcio(String campDescripcio) {
		this.campDescripcio = campDescripcio;
	}

	@Column(name="defproc_jbpmkey", length=255)
	public String getDefprocJbpmKey() {
		return defprocJbpmKey;
	}
	public void setDefprocJbpmKey(String defprocJbpmKey) {
		this.defprocJbpmKey = defprocJbpmKey;
	}

	@Column(name="defproc_versio")
	public int getDefprocVersio() {
		return defprocVersio;
	}
	public void setDefprocVersio(int defprocVersio) {
		this.defprocVersio = defprocVersio;
	}

	@Column(name="tipus", nullable=false)
	public TipusConsultaCamp getTipus() {
		return tipus;
	}
	public void setTipus(TipusConsultaCamp tipus) {
		this.tipus = tipus;
	}

	@Column(name="param_tipus")
	public TipusParamConsultaCamp getParamTipus() {
		return paramTipus;
	}
	public void setParamTipus(TipusParamConsultaCamp paramTipus) {
		this.paramTipus = paramTipus;
	}

	@Column(name="ordre", nullable=false)
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}

	@Column(name="ample_cols")
	public int getAmpleCols() {
		return ampleCols;
	}
	public void setAmpleCols(int ampleCols) {
		this.ampleCols = ampleCols;
	}
	
	@Column(name="buit_cols")
	public int getBuitCols() {
		return buitCols;
	}
	public void setBuitCols(int buitCols) {
		this.buitCols = buitCols;
	}
	@ManyToOne(optional=false)
	@JoinColumn(name="consulta_id")
	@ForeignKey(name="hel_consulta_concamp_fk")
	public Consulta getConsulta() {
		return consulta;
	}
	public void setConsulta(Consulta consulta) {
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
		ConsultaCamp other = (ConsultaCamp) obj;
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
