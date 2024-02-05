package net.conselldemallorca.helium.v3.core.api.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'un paràmetre
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ParametreDto {

	private Long id;
	private String nom;
	private String codi;
	private String descripcio;
	private String valor;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
