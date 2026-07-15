package es.caib.helium.integracio.plugins.procediment;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rolsac2Silencio {
	private String descripcion;
	private Integer codigo;
	private Date fechaBorrar;
	private Boolean hateoasEnabled;
	private String descripcion2;
	private String identificador;
}
