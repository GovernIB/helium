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
public class ProcedimentRolsac {

	private String codigo;
	private String codigoSIA;
	private String nombre;
	private boolean comu;
	@JsonProperty("link_unidadAdministrativa")
	private Link unidadAdministrativa;
}
