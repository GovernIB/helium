package es.caib.helium.back.command;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import es.caib.helium.back.command.EntornAreaMembreCommand.Creacio;
import es.caib.helium.back.command.EntornAreaMembreCommand.Modificacio;
import es.caib.helium.back.validator.EntornAreaMembre;

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
