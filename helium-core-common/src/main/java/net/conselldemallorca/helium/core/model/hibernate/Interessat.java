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

import org.hibernate.annotations.ForeignKey;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;

/**

 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name="hel_interessat")
public class Interessat implements Serializable, GenericEntity<Long> {

	
	private Long id;
	@NotBlank
	private String codi;
	@NotBlank
	private String nif;
	@NotBlank
	private String nom;
	private String llinatge1;  
	private String llinatge2;  
	private String tipus; 
	private String email;  
	private String telefon; 
	
	Expedient expedient;
	
	

	
	
	
	
	public Interessat() {
		super();
	}



	public Interessat(
			Long id, 
			String codi,
			String nom, 
			String nif, 
			String llinatge1, 
			String llinatge2, 
			String tipus,
			String email, 
			String telefon,
			Expedient expedient) {
		super();
		this.id = id;
		this.codi = codi;
		this.nom = nom;
		this.nif = nif;
		this.llinatge1 = llinatge1;
		this.llinatge2 = llinatge2;
		this.tipus = tipus;
		this.email = email;
		this.telefon = telefon;
		this.expedient = expedient;
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
	public String getTipus() {
		return tipus;
	}
	public void setTipus(String tipus) {
		this.tipus = tipus;
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
	private static final long serialVersionUID = 1L;

	



}