package net.conselldemallorca.helium.integracio.plugins.procediment;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/** Classe per rebre el JSON sobre la consutla de servicios de Rolsac2.
 * 
 */
@Getter
@Setter
public class Rolsac2ServiciosResponse {
	private String numeroElementos;
	private String status;
	private Integer tiempo;
	private String mensaje;
	private String url;
	private List<Rolsac2Servei> resultado;
}