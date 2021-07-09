/**
 * 
 */
package es.caib.helium.back.command;

import es.caib.helium.back.validator.EntornCodiNoRepetit;
import javax.validation.constraints.NotEmpty;

import javax.validation.constraints.Size;


/**
 * Command que representa el formulari d'un entorn.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@EntornCodiNoRepetit(groups = {EntornCommand.Creacio.class, EntornCommand.Modificacio.class})
public class EntornCommand {

	private Long id;
	@NotEmpty(groups = {Creacio.class})
	@Size(max = 64, groups = {Creacio.class})
	private String codi;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String nom;
	private String descripcio;
	// Opcions de visualització de la capçalera
	private String colorFons;
	private String colorLletra;

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

	public String getColorFons() {
		return colorFons;
	}
	public void setColorFons(String colorFons) {
		this.colorFons = colorFons;
	}
	public String getColorLletra() {
		return colorLletra;
	}
	public void setColorLletra(String colorLletra) {
		this.colorLletra = colorLletra;
	}

	public interface Creacio {}
	public interface Modificacio {}

}
