/**
 * 
 */
package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;

import net.conselldemallorca.helium.v3.core.api.dto.DadesEnviamentDto.EntregaPostalTipus;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatTipusEnumDto;

/**

 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name="hel_interessat")
public class Interessat implements Serializable, GenericEntity<Long> {

	
	
	private InteressatTipusEnumDto tipus;
	
	private Long id;
	@NotBlank
	private String codi;
	@NotBlank
	private String nif;
	@Column(name = "dir3codi", length = 9)
	private String dir3Codi;
	@NotBlank
	private String nom;
	private String llinatge1;  
	private String llinatge2;  
	private String email;  
	private String telefon; 

	private boolean entregaPostal;
	private EntregaPostalTipus entregaTipus;
	private String linia1;
	private String linia2;
	private String codiPostal;
	private boolean entregaDeh;
	private boolean entregaDehObligat;
	
	private Expedient expedient;

	public Interessat() {
		super();
	}
	public Interessat(
			Long id, 
			String codi,
			String nom, 
			String nif, 
			String dir3codi,
			String llinatge1, 
			String llinatge2, 
			InteressatTipusEnumDto tipus,
			String email, 
			String telefon,
			Expedient expedient,
			boolean entregaPostal,
			EntregaPostalTipus entregaTipus,
			String linia1,
			String linia2,
			String codiPostal,
			boolean entregaDeh,
			boolean entregaDehObligat) {
		super();
		this.id = id;
		this.codi = codi;
		this.nom = nom;
		this.nif = nif;
		this.dir3Codi = dir3codi;
		this.llinatge1 = llinatge1;
		this.llinatge2 = llinatge2;
		this.tipus = tipus;
		this.email = email;
		this.telefon = telefon;
		this.expedient = expedient;
		this.entregaPostal = entregaPostal;
		this.entregaTipus = entregaTipus;
		this.setLinia1(linia1);
		this.setLinia2(linia2);
		this.codiPostal = codiPostal;
		this.entregaDeh = entregaDeh;
		this.entregaDehObligat = entregaDehObligat;
	}
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_tasca")
	@TableGenerator(name="gen_tasca", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
	@ManyToOne(optional=true)
	@JoinColumn(name="expedient_id")
	public Expedient getExpedient() {
		return expedient;
	}
	public void setExpedient(Expedient expedient) {
		this.expedient = expedient;
	}

	public InteressatTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(InteressatTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNif() {
		return nif;
	}
	public void setNif(String nif) {
		this.nif = nif;
	}
	public String getDir3Codi() {
		return dir3Codi;
	}
	public void setDir3Codi(String dir3Codi) {
		this.dir3Codi = dir3Codi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getLlinatge1() {
		return llinatge1;
	}
	public void setLlinatge1(String llinatge1) {
		this.llinatge1 = llinatge1;
	}
	public String getLlinatge2() {
		return llinatge2;
	}
	public void setLlinatge2(String llinatge2) {
		this.llinatge2 = llinatge2;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTelefon() {
		return telefon;
	}
	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}
	public boolean isEntregaPostal() {
		return entregaPostal;
	}
	public void setEntregaPostal(boolean entregaPostal) {
		this.entregaPostal = entregaPostal;
	}
	public EntregaPostalTipus getEntregaTipus() {
		return entregaTipus;
	}
	public void setEntregaTipus(EntregaPostalTipus entregaTipus) {
		this.entregaTipus = entregaTipus;
	}
	public String getLinia1() {
		return linia1;
	}
	public void setLinia1(String linia1) {
		this.linia1 = linia1 != null ? 	linia1.substring(0, Math.min(linia1.length(), 50)) : null;
	}
	public String getLinia2() {
		return linia2;
	}
	public void setLinia2(String linia2) {
		this.linia2 = linia2 != null ? 	linia2.substring(0, Math.min(linia2.length(), 50)) : null;
	}
	public String getCodiPostal() {
		return codiPostal;
	}
	public void setCodiPostal(String codiPostal) {
		this.codiPostal = codiPostal;
	}
	public boolean isEntregaDeh() {
		return entregaDeh;
	}
	public void setEntregaDeh(boolean entregaDeh) {
		this.entregaDeh = entregaDeh;
	}
	public boolean isEntregaDehObligat() {
		return entregaDehObligat;
	}
	public void setEntregaDehObligat(boolean entregaDehObligat) {
		this.entregaDehObligat = entregaDehObligat;
	}
	private static final long serialVersionUID = 1L;

	



}