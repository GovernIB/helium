/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import net.conselldemallorca.helium.v3.core.api.dto.AccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.AccioTipusEnumDto;
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

	@NotNull
	private AccioTipusEnumDto tipus;

	// Tipus handler del flux
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String defprocJbpmKey;
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String jbpmAction;
	
	// Tipus handler propi
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String handlerPropi;
	
	// Tipus handler predefinit Helium
	@Size(max = 255, groups = {Creacio.class, Modificacio.class})
	private String handlerPredefinit;
	
	// Dades del handler propi o predefinit
	private Map<String, String> handlerDades = new HashMap<String, String>();
	
	// Tipus script
	@Size(max = 1024, groups = {Creacio.class, Modificacio.class})
	private String script;
	
	private boolean publica;
	private boolean oculta;
	@Size(max = 512, groups = {Creacio.class, Modificacio.class})
	private String rols;

	private boolean perEstats;

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
	public AccioTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(AccioTipusEnumDto tipus) {
		this.tipus = tipus;
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
	public String getHandlerPropi() {
		return handlerPropi;
	}
	public void setHandlerPropi(String handlerPropi) {
		this.handlerPropi = handlerPropi;
	}
	public String getHandlerPredefinit() {
		return handlerPredefinit;
	}
	public void setHandlerPredefinit(String handlerPredefinit) {
		this.handlerPredefinit = handlerPredefinit;
	}
	public Map<String, String> getHandlerDades() {
		return handlerDades;
	}
	public void setHandlerDades(Map<String, String> handlerDades) {
		this.handlerDades = handlerDades;
	}
	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
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
	public boolean isPerEstats() {
		return perEstats;
	}
	public void setPerEstats(boolean perEstats) {
		this.perEstats = perEstats;
	}

	public static AccioDto asAccioDto(ExpedientTipusAccioCommand command) {
		AccioDto dto = new AccioDto();
		
		dto.setId(command.getId());
		dto.setCodi(command.getCodi());
		dto.setNom(command.getNom());
		dto.setTipus(command.getTipus());
		dto.setDefprocJbpmKey(command.getDefprocJbpmKey());
		dto.setJbpmAction(command.getJbpmAction());
		dto.setDescripcio(command.getDescripcio());
		switch(command.getTipus()) {
			case HANDLER_PROPI:
				dto.setHandlerClasse(command.getHandlerPropi());
				break;
			case HANDLER_PREDEFINIT:
				dto.setHandlerClasse(command.getHandlerPredefinit());
				break;
			default:
				break;		
		}
		try {
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			dto.setHandlerDades(
					ow.writeValueAsString(command.getHandlerDades()));
		} catch(Exception e) {
			/* L'error de conversió s'hauria d'haver detectat a la validació */
			dto.setHandlerDades("[{'error': 'Error convertint les dades a JSON'}]");
		}

		dto.setScript(command.getScript());
		dto.setPublica(command.isPublica());
		dto.setOculta(command.isOculta());
		dto.setRols(command.getRols());	
		
		return dto;
	}
	
	public String getHandlerDadesJson() {
	
		String json = "[]";

		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		try {
			json = ow.writeValueAsString(handlerDades);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return json;
	}

	public interface Creacio {}
	public interface Modificacio {}
}
