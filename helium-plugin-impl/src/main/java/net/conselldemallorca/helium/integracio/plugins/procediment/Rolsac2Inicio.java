package net.conselldemallorca.helium.integracio.plugins.procediment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rolsac2Inicio {
	private String descripcion;
	private Integer codigo;
	private Boolean hateoasEnabled;
	private String identificador;
}
