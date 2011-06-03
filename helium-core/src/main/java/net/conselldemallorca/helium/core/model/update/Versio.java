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
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

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
	@NotNull
	private Integer ordre;
	@MaxLength(255)
	private String descripcio;
	private Date dataExecucio = new Date();
	private boolean procesExecutat = false;
	
	private static String version = "";
	private static String error = "";


	public Versio() {}
	public Versio(String codi, Integer ordre) {
		this.codi = codi;
		this.ordre = ordre;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_versio")
	@TableGenerator(name="gen_versio", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
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

	@Column(name="ordre", length=64, nullable=false, unique=true)
	public Integer getOrdre() {
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
	
	@Column(name="data_execucio")
	public Date getDataExecucio() {
		return dataExecucio;
	}
	public void setDataExecucio(Date dataExecucio) {
		this.dataExecucio = dataExecucio;
	}

	@Column(name="proces_executat")
	public boolean isProcesExecutat() {
		return procesExecutat;
	}
	public void setProcesExecutat(boolean procesExecutat) {
		this.procesExecutat = procesExecutat;
	}

	@Transient
	public static String getVersion() {
		return version;
	}
	public static void setVersion(String version) {
		Versio.version = version;
	}
	@Transient
	public static String getError() {
		return error;
	}
	public static void setError(String error) {
		Versio.error = error;
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
