package net.conselldemallorca.helium.integracio.plugins.procediment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rolsac2TipoProcedimiento {
	private String descripcion;
	private Integer codigo;
	private Link link_entidad;
	private Boolean hateoasEnabled;
	private Integer entidad;
	private String identificador;
}
