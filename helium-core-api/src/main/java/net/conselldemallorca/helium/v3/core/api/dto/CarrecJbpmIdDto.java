package net.conselldemallorca.helium.v3.core.api.dto;

import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto.Sexe;

public class CarrecJbpmIdDto {
	
	private Long id;
	private String codi;
	private String descripcio;
	private String grup;
	private String nomDona;
	private String nomHome;
	private Sexe personaSexe; 
	private String carrecDona;
	private String  tractamentHome;
	private String  tractamentDona;
	
	private int personaSexeId;
	
	public int getPersonaSexeId() {
		return personaSexeId;
	}
	public void setPersonaSexeId(int personaSexeId) {
		this.personaSexeId = personaSexeId;
	}

	
	public Sexe getPersonaSexe() {
		return personaSexe;
	}
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
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public String getGrup() {
		return grup;
	}
	public void setGrup(String grup) {
		this.grup = grup;
	}
	public String getNomDona() {
		return nomDona;
	}
	public void setNomDona(String nomDona) {
		this.nomDona = nomDona;
	}
	public String getNomHome() {
		return nomHome;
	}
	public void setNomHome(String nomHome) {
		this.nomHome = nomHome;
	}
	public Sexe isPersonaSexe() {
		return personaSexe;
	}
	public void setPersonaSexe(Sexe personaSexe) {
		this.personaSexe = personaSexe;
	}
	public String getCarrecDona() {
		return carrecDona;
	}
	public void setCarrecDona(String carrecDona) {
		this.carrecDona = carrecDona;
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
	
}
