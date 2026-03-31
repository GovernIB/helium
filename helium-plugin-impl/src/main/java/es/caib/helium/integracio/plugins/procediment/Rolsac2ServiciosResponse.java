package es.caib.helium.integracio.plugins.procediment;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/** Classe per rebre el JSON sobre la consutla de servicios de Rolsac2.
 * 
 */
@Getter
@Setter
public class Rolsac2ServiciosResponse {
	private String status;
	private Integer tiempo;
	private String mensaje;
	private String resultadoURL;
	private String resultadoLong;
	private String dateDownload;
	private Integer totalCount;
	private Integer itemsReturned;
	private Integer pageSize;
	private Integer totalPages;
	private Integer page;
	private List<Rolsac2Servei> items;
}