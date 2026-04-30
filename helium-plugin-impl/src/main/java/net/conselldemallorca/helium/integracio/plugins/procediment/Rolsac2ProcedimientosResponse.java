package net.conselldemallorca.helium.integracio.plugins.procediment;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

/** Classe per rebre el JSON sobre la consutla de procediments de Rolsac2.
 * 
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rolsac2ProcedimientosResponse {
	private String dateDownload;
	private String numeroElementos;
	private Integer tiempo;
	private String mensaje;
	private String status;
	private String url;
	
	private Integer totalCount;
	private Integer itemsReturned;
	private String pageSize;
	private Integer totalPages;
	private Integer page;
	
	private String resultadoURL;
	private Integer resultadoLong;
	
	private List<Rolsac2Procediment> items;
}