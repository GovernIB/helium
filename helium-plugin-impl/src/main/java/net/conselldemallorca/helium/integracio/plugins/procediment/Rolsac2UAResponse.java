package net.conselldemallorca.helium.integracio.plugins.procediment;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/** Classe per rebre el JSON sobre la consutla de procediments de Rolsac2.
 * 
 */
@Getter
@Setter
public class Rolsac2UAResponse {
	private String numeroElementos;
	private String status;
	private String mensaje;
	private Integer tiempo;
	private List<Rolsac2UnitatAdministrativa> resultado;
}