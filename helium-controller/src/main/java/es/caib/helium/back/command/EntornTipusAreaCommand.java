package es.caib.helium.back.command;

import es.caib.helium.back.validator.EntornTipusArea;
import javax.validation.constraints.NotEmpty;

import javax.validation.constraints.Size;

/**
 * Command que representa el formulari d'un tipus d'àrea.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@EntornTipusArea(groups = { EntornTipusAreaCommand.Creacio.class, EntornTipusAreaCommand.Modificacio.class })
public class EntornTipusAreaCommand {

	private Long id;
	@NotEmpty(groups = { Creacio.class })
	@Size(max = 64, groups = { Creacio.class })
	private String codi;
	@NotEmpty(groups = { Creacio.class, Modificacio.class })
	@Size(max = 255, groups = { Creacio.class, Modificacio.class })
	private String nom;
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

	public String getDescripcio() {
		return descripcio;
	}

	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}

	public interface Creacio {
	}

	public interface Modificacio {
	}
}
