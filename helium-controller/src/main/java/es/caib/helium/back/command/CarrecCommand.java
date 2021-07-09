package es.caib.helium.back.command;

import es.caib.helium.back.validator.Carrec;
import javax.validation.constraints.NotEmpty;

import javax.validation.constraints.Size;

/**
 * Command que representa el formulari d'una àrea.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Carrec(groups = { CarrecCommand.Creacio.class, CarrecCommand.Modificacio.class })
public class CarrecCommand {
	
	private Long id;
	@NotEmpty(groups = {Creacio.class})
	@Size(max = 64, groups = {Creacio.class})
	private String codi;
	@NotEmpty(groups = { Creacio.class, Modificacio.class })
	@Size(max = 255, groups = { Creacio.class, Modificacio.class })
	private String grup;
	@NotEmpty(groups = { Creacio.class, Modificacio.class })
	@Size(max = 255, groups = { Creacio.class, Modificacio.class })
	private String nomHome;
	@NotEmpty(groups = { Creacio.class, Modificacio.class })
	@Size(max = 255, groups = { Creacio.class, Modificacio.class })
	private String nomDona;
	@NotEmpty(groups = { Creacio.class, Modificacio.class })
	@Size(max = 255, groups = { Creacio.class, Modificacio.class })
	private String tractamentHome;
	@NotEmpty(groups = { Creacio.class, Modificacio.class })
	@Size(max = 255, groups = { Creacio.class, Modificacio.class })
	private String tractamentDona;
	private Long personaSexeId;
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
	public String getGrup() {
		return grup;
	}
	public void setGrup(String grup) {
		this.grup = grup;
	}
	public String getNomHome() {
		return nomHome;
	}
	public void setNomHome(String nomHome) {
		this.nomHome = nomHome;
	}
	public String getNomDona() {
		return nomDona;
	}
	public void setNomDona(String nomDona) {
		this.nomDona = nomDona;
	}
	public String getTractamentHome() {
		return tractamentHome;
	}
	public void setTractamentHome(String tractamentHome) {
		this.tractamentHome = tractamentHome;
	}
	public String getTractamentDona() {
		return tractamentDona;
	}
	public void setTractamentDona(String tractamentDona) {
		this.tractamentDona = tractamentDona;
	}
	public Long getPersonaSexeId() {
		return personaSexeId;
	}
	public void setPersonaSexeId(Long personaSexeId) {
		this.personaSexeId = personaSexeId;
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
