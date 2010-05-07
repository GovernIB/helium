/**
 * 
 */
package net.conselldemallorca.helium.security.audit;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import net.conselldemallorca.helium.model.hibernate.GenericEntity;

/**
 * Classe base per guardar els logs de les accions
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Entity
@Table(name="hel_action_log")
public class ActionLog implements Serializable, GenericEntity<Long> {

	private Long id;
	private String usuari;
	private Date data;
	private String taula;
	private String columnaPk;
	private String accio;
	private String valors;



	public ActionLog() {}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_action_log")
	@TableGenerator(name="gen_action_log", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="usuari", nullable=false)
	public String getUsuari() {
		return usuari;
	}
	public void setUsuari(String usuari) {
		this.usuari = usuari;
	}

	@Column(name="data", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}

	@Column(name="taula", nullable=false)
	public String getTaula() {
		return taula;
	}
	public void setTaula(String taula) {
		this.taula = taula;
	}

	@Column(name="columna_pk", nullable=false)
	public String getColumnaPk() {
		return columnaPk;
	}
	public void setColumnaPk(String columnaPk) {
		this.columnaPk = columnaPk;
	}

	@Column(name="accio", nullable=false)
	public String getAccio() {
		return accio;
	}
	public void setAccio(String accio) {
		this.accio = accio;
	}

	@Column(name="valors", nullable=false)
	public String getValors() {
		return valors;
	}
	public void setValors(String valors) {
		this.valors = valors;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accio == null) ? 0 : accio.hashCode());
		result = prime * result
				+ ((columnaPk == null) ? 0 : columnaPk.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((taula == null) ? 0 : taula.hashCode());
		result = prime * result + ((usuari == null) ? 0 : usuari.hashCode());
		result = prime * result + ((valors == null) ? 0 : valors.hashCode());
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
		ActionLog other = (ActionLog) obj;
		if (accio == null) {
			if (other.accio != null)
				return false;
		} else if (!accio.equals(other.accio))
			return false;
		if (columnaPk == null) {
			if (other.columnaPk != null)
				return false;
		} else if (!columnaPk.equals(other.columnaPk))
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (taula == null) {
			if (other.taula != null)
				return false;
		} else if (!taula.equals(other.taula))
			return false;
		if (usuari == null) {
			if (other.usuari != null)
				return false;
		} else if (!usuari.equals(other.usuari))
			return false;
		if (valors == null) {
			if (other.valors != null)
				return false;
		} else if (!valors.equals(other.valors))
			return false;
		return true;
	}

	private static final long serialVersionUID = 1L;

}
