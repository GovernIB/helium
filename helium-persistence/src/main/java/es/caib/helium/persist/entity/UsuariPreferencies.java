package es.caib.helium.persist.entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Objecte de domini que representa les preferències d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name="hel_usuari_prefs")
public class UsuariPreferencies implements Serializable, GenericEntity<String> {

	@NotBlank
	@Size(max = 64)
	private String codi;
	@Size(max = 64)
	private String defaultEntornCodi;
	@Size(max = 255)
	private String idioma;
	private Boolean cabeceraReducida;
	private Integer listado;
	@Size(max = 255)
	private Long consultaId;
	@Size(max = 255)
	private Long expedientTipusDefecteId;	
	private Boolean filtroTareasActivas;
	@Size(max = 255)
	private Long numElementosPagina;

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
	@Column(name="cabecera_reducida", nullable=true)
	public Boolean isCabeceraReducida() {
		return cabeceraReducida;
	}
	public void setCabeceraReducida(Boolean cabeceraReducida) {
		this.cabeceraReducida = cabeceraReducida;
	}
	@Column(name="default_tipus_expedient", length=255, nullable=true)
	public Long getExpedientTipusDefecteId() {
		return expedientTipusDefecteId;
	}
	public void setExpedientTipusDefecteId(Long expedientTipusDefecteId) {
		this.expedientTipusDefecteId = expedientTipusDefecteId;
	}
	@Column(name="listado",nullable=true)
	public Integer getListado() {
		return listado;
	}
	public void setListado(Integer listado) {
		this.listado = listado;
	}
	@Column(name="consulta_id", length=255, nullable=true)
	public Long getConsultaId() {
		return consultaId;
	}
	public void setConsultaId(Long consultaId) {
		this.consultaId = consultaId;
	}
	@Column(name="filtro_tareas_activas", nullable=true)
	public Boolean isFiltroTareasActivas() {
		return filtroTareasActivas;
	}
	public void setFiltroTareasActivas(Boolean filtroTareasActivas) {
		this.filtroTareasActivas = filtroTareasActivas;
	}
	@Column(name="num_elementos_pagina", length=255, nullable=true)
	public Long getNumElementosPagina() {
		return numElementosPagina;
	}
	public void setNumElementosPagina(Long numElementosPagina) {
		this.numElementosPagina = numElementosPagina;
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
