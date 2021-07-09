package es.caib.helium.back.command;

import es.caib.helium.back.validator.EntornArea;
import javax.validation.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Command que representa el formulari d'una àrea.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@EntornArea(groups = { EntornAreaCommand.Creacio.class, EntornAreaCommand.Modificacio.class })
public class EntornAreaCommand {

	private Long id;
	@NotEmpty(groups = {Creacio.class})
	@Size(max = 64, groups = {Creacio.class})
	private String codi;
	@NotEmpty(groups = { Creacio.class, Modificacio.class })
	@Size(max = 255, groups = { Creacio.class, Modificacio.class })
	private String nom;
	@Size(max = 255, groups = { Creacio.class, Modificacio.class })
	private String descripcio;
	private Long pareId;
	@NotNull(groups = {Creacio.class, Modificacio.class})
	private Long tipusId;

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
	public Long getPareId() {
		return pareId;
	}
	public void setPareId(Long pareId) {
		this.pareId = pareId;
	}
	public Long getTipusId() {
		return tipusId;
	}
	public void setTipusId(Long tipusId) {
		this.tipusId = tipusId;
	}

	public interface Creacio {};
	public interface Modificacio {};
}
