/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import net.conselldemallorca.helium.v3.core.api.dto.AccioDto;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusAccioCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusAccioCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.validator.Codi;
import net.conselldemallorca.helium.webapp.v3.validator.ExpedientTipusAccio;

/**
 * Command per editar la informació de les accions dels tipus d'expedient 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@ExpedientTipusAccio(groups = {Creacio.class, Modificacio.class})
public class ExpedientTipusAccioCommand {
	
	private Long expedientTipusId;
	private Long id;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 64, groups = {Creacio.class, Modificacio.class})
	@Codi(groups = {Creacio.class, Modificacio.class})
	private String codi;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String nom;
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String descripcio;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String jbpmAction;
	private boolean publica;
	private boolean oculta;
	@Size(max = 512, groups = {Creacio.class, Modificacio.class})
	private String rols;
	
	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}
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
	public String getJbpmAction() {
		return jbpmAction;
	}
	public void setJbpmAction(String jbpmAction) {
		this.jbpmAction = jbpmAction;
	}
	public boolean isPublica() {
		return publica;
	}
	public void setPublica(boolean publica) {
		this.publica = publica;
	}
	public boolean isOculta() {
		return oculta;
	}
	public void setOculta(boolean oculta) {
		this.oculta = oculta;
	}
	public String getRols() {
		return rols;
	}
	public void setRols(String rols) {
		this.rols = rols;
	}
	
	public static AccioDto asAccioDto(ExpedientTipusAccioCommand command) {
		AccioDto dto = new AccioDto();
		
		dto.setId(command.getId());
		dto.setCodi(command.getCodi());
		dto.setNom(command.getNom());
		dto.setJbpmAction(command.getJbpmAction());
		dto.setDescripcio(command.getDescripcio());
		dto.setPublica(command.isPublica());
		dto.setOculta(command.isOculta());
		dto.setRols(command.getRols());			
		
		return dto;
	}

	public interface Creacio {}
	public interface Modificacio {}
}
