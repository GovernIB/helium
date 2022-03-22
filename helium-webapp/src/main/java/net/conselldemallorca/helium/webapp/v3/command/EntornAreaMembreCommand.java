package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import net.conselldemallorca.helium.webapp.v3.command.EntornAreaMembreCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.EntornAreaMembreCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.validator.EntornAreaMembre;

/**
 * Command que representa el formulari nou membre de l'àrea
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@EntornAreaMembre(groups = { Creacio.class, Modificacio.class })
public class EntornAreaMembreCommand {
	
	private Long id;
	@NotEmpty(groups = {Creacio.class})
	@Size(max = 64, groups = {Creacio.class})
	private String codi;
//	@NotNull(groups = {Creacio.class, Modificacio.class})
	private Long areaId;
	private Long carrecId;
	
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
	public Long getAreaId() {
		return areaId;
	}
	public void setAreaId(Long areaId) {
		this.areaId = areaId;
	}
	public Long getCarrecId() {
		return carrecId;
	}
	public void setCarrecId(Long carrecId) {
		this.carrecId = carrecId;
	}
	
	public interface Creacio {};
	public interface Modificacio {};
}