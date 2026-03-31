package es.caib.helium.integracio.plugins.procediment;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * RespuestaBase. Estructura de respuesta que contiene la información comun a todas las respuestas.
 * 
 */
@Getter @Setter
public class Link {
	 
	@JsonProperty("rel")
	private String rel;
	@JsonProperty("codigo")
	private String codigo;
	@JsonProperty("href")
	private String href;
	@JsonProperty("descripcion")
	private String descripcion;

}
