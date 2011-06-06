package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;

/**
 * Objecte de domini que representa les preferències d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="hel_usuari_prefs")
public class UsuariPreferencies implements Serializable, GenericEntity<String> {

	@NotBlank
	@MaxLength(64)
	private String codi;
	@MaxLength(64)
	private String defaultEntornCodi;
	@MaxLength(255)
	private String idioma;



	public UsuariPreferencies() {}
	public UsuariPreferencies(String codi) {
		this.codi = codi;
	}

	@Id
	@Column(name="codi", length=64)
	public String getCodi() {
		return this.codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	@Transient
	public String getId() {
		return this.codi;
	}

	@Column(name="default_entorn", length=64, nullable=true)
	public String getDefaultEntornCodi() {
		return defaultEntornCodi;
	}
	public void setDefaultEntornCodi(String defaultEntornCodi) {
		this.defaultEntornCodi = defaultEntornCodi;
	}
	
	@Column(name="idioma", length=255, nullable=true)
	public String getIdioma() {
		return idioma;
	}
	public void setIdioma(String idioma) {
		this.idioma = idioma;
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
		UsuariPreferencies other = (UsuariPreferencies) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Usuari codi=" + codi;
	}

	private static final long serialVersionUID = 1L;

}
