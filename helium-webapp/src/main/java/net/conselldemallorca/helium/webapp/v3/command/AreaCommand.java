package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import net.conselldemallorca.helium.webapp.v3.command.AreaCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.AreaCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.validator.Area;

/**
 * Command que representa el formulari d'una àrea.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Area(groups = { Creacio.class, Modificacio.class })
public class AreaCommand {
	
	private Long id;
	@NotEmpty(groups = {Creacio.class})
	@Size(max = 64, groups = {Creacio.class})
	private String codi;
	@NotEmpty(groups = { Creacio.class, Modificacio.class })
	@Size(max = 255, groups = { Creacio.class, Modificacio.class })
	private String nom;
	private Long personaSexe;
	@Size(max = 255, groups = { Creacio.class, Modificacio.class })
	private String descripcio;

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
	public Long getPersonaSexe() {
		return personaSexe;
	}
	public void setPersonaSexe(Long personaSexe) {
		this.personaSexe = personaSexe;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	
	public interface Creacio {};
	public interface Modificacio {};
}
