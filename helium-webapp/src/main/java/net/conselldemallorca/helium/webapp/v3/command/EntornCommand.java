/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import net.conselldemallorca.helium.webapp.v3.command.EntornCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.EntornCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.validator.EntornCodiNoRepetit;

/**
 * Command que representa el formulari d'un entorn.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@EntornCodiNoRepetit(groups = {Creacio.class, Modificacio.class})
public class EntornCommand {

	private Long id;
	@NotEmpty(groups = {Creacio.class})
	@Size(max = 64, groups = {Creacio.class})
	private String codi;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String nom;
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
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}

	public interface Creacio {}
	public interface Modificacio {}

}
