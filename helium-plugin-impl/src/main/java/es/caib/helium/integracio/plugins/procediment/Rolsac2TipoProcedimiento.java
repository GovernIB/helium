package es.caib.helium.integracio.plugins.procediment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Rolsac2TipoProcedimiento {
	private String descripcion;
	private Integer codigo;
	private Link link_entidad;
	private Boolean hateoasEnabled;
	private Integer entidad;
	private String identificador;
}
