package net.conselldemallorca.helium.integracio.plugins.procediment;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'un procediment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ServeiRolsac {
	private String codigo;
	private String codigoSIA;
	private String nombre;
	private boolean comu;
	@JsonProperty("link_organoInstructor")
	private Link organoInstructor;
}
