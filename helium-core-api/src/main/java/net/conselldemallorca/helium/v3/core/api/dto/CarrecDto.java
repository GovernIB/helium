/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto.Sexe;

/**
 * DTO amb informació d'un càrrec.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CarrecDto {

	private Long id;
	private String codi;
	private String nomHome;
	private String nomDona;
	private String tractamentHome;
	private String tractamentDona;
	private String descripcio;
	private String personaCodi;
	
	private Sexe personaSexe;
	private Long areaId;
	private EntornAreaDto area;

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
	public String getNomHome() {
		return nomHome;
	}
	public void setNomHome(String nomHome) {
		this.nomHome = nomHome;
	}
	public String getNomDona() {
		return nomDona;
	}
	public void setNomDona(String nomDona) {
		this.nomDona = nomDona;
	}
	public String getTractamentHome() {
		return tractamentHome;
	}
	public void setTractamentHome(String tractamentHome) {
		this.tractamentHome = tractamentHome;
	}
	public String getTractamentDona() {
		return tractamentDona;
	}
	public void setTractamentDona(String tractamentDona) {
		this.tractamentDona = tractamentDona;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public String getPersonaCodi() {
		return personaCodi;
	}
	public void setPersonaCodi(String personaCodi) {
		this.personaCodi = personaCodi;
	}
	public Long getAreaId() {
		return areaId;
	}
	public void setAreaId(Long areaId) {
		this.areaId = areaId;
	}
	public Sexe getPersonaSexe() {
		return personaSexe;
	}
	public void setPersonaSexe(Sexe personaSexe) {
		this.personaSexe = personaSexe;
	}
	
	public EntornAreaDto getArea() {
		return area;
	}
	public void setArea(EntornAreaDto area) {
		this.area = area;
	}
	
}
