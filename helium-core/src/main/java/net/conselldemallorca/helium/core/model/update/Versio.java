/**
 * 
 */
package net.conselldemallorca.helium.core.model.update;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import net.conselldemallorca.helium.core.model.hibernate.GenericEntity;
import net.conselldemallorca.helium.core.security.acl.SecureObject;

import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

/**
 * Objecte de domini que representa una versió de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="hel_versio")
public class Versio implements Serializable, GenericEntity<Long>, SecureObject {

	private Long id;
	@NotBlank
	@MaxLength(64)
	private String codi;
	private int ordre;
	@MaxLength(255)
	private String descripcio;
	@NotNull
	private Date dataCreacio;
	private boolean procesExecutat = false;
	private Date dataExecucioProces;
	private boolean scriptExecutat = false;
	private Date dataExecucioScript;



	public Versio() {}
	public Versio(String codi, Integer ordre) {
		this.codi = codi;
		this.ordre = ordre;
		this.setDataCreacio(new Date());
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="codi", length=64, nullable=false, unique=true)
	public String getCodi() {
		return this.codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}

	@Column(name="ordre", nullable=false, unique=true)
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(Integer ordre) {
		this.ordre = ordre;
	}

	@Column(name="descripcio", length=255)
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}

	@Column(name="data_creacio", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataCreacio() {
		return dataCreacio;
	}
	public void setDataCreacio(Date dataCreacio) {
		this.dataCreacio = dataCreacio;
	}

	@Column(name="proces_executat")
	public boolean isProcesExecutat() {
		return procesExecutat;
	}
	public void setProcesExecutat(boolean procesExecutat) {
		this.procesExecutat = procesExecutat;
	}

	@Column(name="data_execucio_proces")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataExecucioProces() {
		return dataExecucioProces;
	}
	public void setDataExecucioProces(Date dataExecucioProces) {
		this.dataExecucioProces = dataExecucioProces;
	}

	@Column(name="script_executat")
	public boolean isScriptExecutat() {
		return scriptExecutat;
	}
	public void setScriptExecutat(boolean scriptExecutat) {
		this.scriptExecutat = scriptExecutat;
	}

	@Column(name="data_execucio_script")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataExecucioScript() {
		return dataExecucioScript;
	}
	public void setDataExecucioScript(Date dataExecucioScript) {
		this.dataExecucioScript = dataExecucioScript;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
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
		Versio other = (Versio) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Versio id=" + id + ", codi=" + codi + ", ordre=" + ordre;
	}

	private static final long serialVersionUID = 1L;

}
