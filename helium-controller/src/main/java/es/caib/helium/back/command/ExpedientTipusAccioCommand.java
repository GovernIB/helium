/**
 * 
 */
package es.caib.helium.back.command;

import es.caib.helium.back.validator.Codi;
import es.caib.helium.back.validator.ExpedientTipusAccio;
import es.caib.helium.logic.intf.dto.AccioDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * Command per editar la informació de les accions dels tipus d'expedient 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@ExpedientTipusAccio(groups = {ExpedientTipusAccioCommand.Creacio.class, ExpedientTipusAccioCommand.Modificacio.class})
public class ExpedientTipusAccioCommand {
	
	private Long expedientTipusId;
	private Long definicioProcesId;
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
	private String defprocJbpmKey;
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
	public Long getDefinicioProcesId() {
		return definicioProcesId;
	}
	public void setDefinicioProcesId(Long definicioProcesId) {
		this.definicioProcesId = definicioProcesId;
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
	public String getDefprocJbpmKey() {
		return defprocJbpmKey;
	}
	public void setDefprocJbpmKey(String defprocJbpmKey) {
		this.defprocJbpmKey = defprocJbpmKey;
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
		dto.setDefprocJbpmKey(command.getDefprocJbpmKey());
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
