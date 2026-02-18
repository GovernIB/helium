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
	private Integer tiempo;
	private Integer itemsReturned;
	private String resultadoURL;
	private Integer totalPages;
	private String pageSize;
	private Integer page;
	private String mensaje;
	private Integer totalCount;
	private String dateDownload;
	private String status;
	private Integer resultadoLong;
	
	private List<Rolsac2UnitatAdministrativa> items;
}