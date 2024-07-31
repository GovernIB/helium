package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;
import java.util.Date;

public class ServeiPinbalDto implements Serializable {

	private static final long serialVersionUID = -2212268159370233660L;
	
	private Long id;
	private String nom;	
	private PinbalServeiEnumDto codi;	
	private boolean pinbalServeiDocPermesDni;
	private boolean pinbalServeiDocPermesNif;
	private boolean pinbalServeiDocPermesCif;
	private boolean pinbalServeiDocPermesNie;
	private boolean pinbalServeiDocPermesPas;
    private Date createdDate;
    private boolean actiu;
    private Date updatedDate;
	private String updatedUsuari;
    
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public boolean isActiu() {
		return actiu;
	}
	public void setActiu(boolean actiu) {
		this.actiu = actiu;
	}
	public PinbalServeiEnumDto getCodi() {
		return codi;
	}
	public void setCodi(PinbalServeiEnumDto codi) {
		this.codi = codi;
	}
	public boolean isPinbalServeiDocPermesDni() {
		return pinbalServeiDocPermesDni;
	}
	public void setPinbalServeiDocPermesDni(boolean pinbalServeiDocPermesDni) {
		this.pinbalServeiDocPermesDni = pinbalServeiDocPermesDni;
	}
	public boolean isPinbalServeiDocPermesNif() {
		return pinbalServeiDocPermesNif;
	}
	public void setPinbalServeiDocPermesNif(boolean pinbalServeiDocPermesNif) {
		this.pinbalServeiDocPermesNif = pinbalServeiDocPermesNif;
	}
	public boolean isPinbalServeiDocPermesCif() {
		return pinbalServeiDocPermesCif;
	}
	public void setPinbalServeiDocPermesCif(boolean pinbalServeiDocPermesCif) {
		this.pinbalServeiDocPermesCif = pinbalServeiDocPermesCif;
	}
	public boolean isPinbalServeiDocPermesNie() {
		return pinbalServeiDocPermesNie;
	}
	public void setPinbalServeiDocPermesNie(boolean pinbalServeiDocPermesNie) {
		this.pinbalServeiDocPermesNie = pinbalServeiDocPermesNie;
	}
	public boolean isPinbalServeiDocPermesPas() {
		return pinbalServeiDocPermesPas;
	}
	public void setPinbalServeiDocPermesPas(boolean pinbalServeiDocPermesPas) {
		this.pinbalServeiDocPermesPas = pinbalServeiDocPermesPas;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Date getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	public String getUpdatedUsuari() {
		return updatedUsuari;
	}
	public void setUpdatedUsuari(String updatedUsuari) {
		this.updatedUsuari = updatedUsuari;
	}
    
    
}
