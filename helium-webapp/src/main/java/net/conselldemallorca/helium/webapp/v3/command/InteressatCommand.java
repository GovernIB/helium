/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;

import net.conselldemallorca.helium.webapp.v3.command.InteressatCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.InteressatCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.validator.EntornCodiNoRepetit;
import net.conselldemallorca.helium.webapp.v3.validator.ExpedientTipusConsulta;
import net.conselldemallorca.helium.webapp.v3.validator.InteressatCodiNoRepetit;

/**
 * Command que representa el formulari d'un entorn.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@InteressatCodiNoRepetit(groups = {Creacio.class, Modificacio.class})
public class InteressatCommand {

	private Long id;
	@NotEmpty(groups = {Creacio.class})
	@Size(max = 64, groups = {Creacio.class})
	private String codi;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String nom;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 16, groups = {Creacio.class, Modificacio.class})
	private String nif;
	private String llinatge1;  
	private String llinatge2;  
	private String tipus; 
	private String email;  
	private Long expedientId;
	
	
	public Long getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}
	public String getNif() {
		return nif;
	}
	public void setNif(String nif) {
		this.nif = nif;
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
	private String telefon; 

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
	

	
	public interface Creacio {}
	public interface Modificacio {}

}
