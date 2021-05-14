package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import net.conselldemallorca.helium.webapp.v3.command.EntornCarrecCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.EntornCarrecCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.validator.EntornCarrec;

/**
 * Command que representa el formulari d'un càrrec.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@EntornCarrec(groups = { Creacio.class, Modificacio.class })
public class EntornCarrecCommand {
	
	private Long id;
	@NotEmpty(groups = {Creacio.class})
	@Size(max = 64, groups = {Creacio.class})
	private String codi;
	@Size(max = 255, groups = { Creacio.class, Modificacio.class })
	private String descripcio;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 255, groups = { Creacio.class, Modificacio.class })
	private String nomHome;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 255, groups = { Creacio.class, Modificacio.class })
	private String nomDona;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 255, groups = { Creacio.class, Modificacio.class })
	private String tractamentHome;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 255, groups = { Creacio.class, Modificacio.class })
	private String tractamentDona;
	@NotNull(groups = {Creacio.class, Modificacio.class})
	private Long areaId;
	
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
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
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
	public Long getAreaId() {
		return areaId;
	}
	public void setAreaId(Long areaId) {
		this.areaId = areaId;
	}
	
	public interface Creacio {};
	public interface Modificacio {};
}
