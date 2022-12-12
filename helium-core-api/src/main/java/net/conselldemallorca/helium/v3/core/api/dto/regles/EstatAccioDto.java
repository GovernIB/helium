package net.conselldemallorca.helium.v3.core.api.dto.regles;

import net.conselldemallorca.helium.v3.core.api.dto.AccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;

public class EstatAccioDto {
	
	private Long id;
    private int ordre;
    private String nom;
    private EstatDto estat;
    private AccioDto accio;

    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public EstatDto getEstat() {
		return estat;
	}
	public void setEstat(EstatDto estat) {
		this.estat = estat;
	}
	public AccioDto getAccio() {
		return accio;
	}
	public void setAccio(AccioDto accio) {
		this.accio = accio;
	}
}
