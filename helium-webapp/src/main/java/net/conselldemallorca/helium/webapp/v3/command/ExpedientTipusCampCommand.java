/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusCampCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusCampCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.validator.ExpedientTipusCamp;

/**
 * Command per editar la informació de les varialbes dels tipus d'expedient 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@ExpedientTipusCamp(groups = {Creacio.class, Modificacio.class})
public class ExpedientTipusCampCommand {
	
	private Long expedientTipusId;
	private Long id;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 64, groups = {Creacio.class})
	private String codi;
	@NotNull(groups = {Creacio.class, Modificacio.class})
	private CampTipusDto tipus;
	@NotEmpty(groups = {Creacio.class, Modificacio.class})
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String etiqueta;
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String observacions;
	private Long dominiId;
	private boolean multiple;
		
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
	public CampTipusDto getTipus() {
		return tipus;
	}
	public void setTipus(CampTipusDto tipus) {
		this.tipus = tipus;
	}
	public String getEtiqueta() {
		return etiqueta;
	}
	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	public Long getDominiId() {
		return dominiId;
	}
	public void setDominiId(Long dominiId) {
		this.dominiId = dominiId;
	}
	public boolean isMultiple() {
		return multiple;
	}
	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}

	public interface Creacio {}
	public interface Modificacio {}
}
