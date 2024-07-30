package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import net.conselldemallorca.helium.v3.core.api.dto.PinbalServeiEnumDto;

@Entity
@Table(name="HEL_PINBAL_SERVEI")
public class ServeiPinbalEntity implements Serializable {

	private static final long serialVersionUID = -7690890549105085427L;

	@Id
	@Column(name="id")
	private Long id;
	@Column(name="nom")
	private String nom;	
	@Column(name="codi", nullable=false, updatable= false)
	@Enumerated(EnumType.STRING)
	private PinbalServeiEnumDto codi;
	@Column(name = "doc_permes_dni", nullable = false)
	private boolean pinbalServeiDocPermesDni;
	@Column(name = "doc_permes_nif", nullable = false)
	private boolean pinbalServeiDocPermesNif;
	@Column(name = "doc_permes_cif", nullable = false)
	private boolean pinbalServeiDocPermesCif;
	@Column(name = "doc_permes_nie", nullable = false)
	private boolean pinbalServeiDocPermesNie;
	@Column(name = "doc_permes_pas", nullable = false)
	private boolean pinbalServeiDocPermesPas;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
	@Column(name = "actiu", nullable = false)
	private boolean actiu;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;
	@Column(name="UPDATEDUSUARI")
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