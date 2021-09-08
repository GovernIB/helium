package net.conselldemallorca.helium.api.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Informació de l'expedient amb les propietats necessàries pel llistat filtrat i paginat
 * d'expedients.
 * 
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcesDto {

	private Long id;
	private String procesId;
	private Long expedientId;
	private String motor;
	private String processDefinitionId;
	private String procesArrelId;
	private String procesPareId;
	private String descripcio;
	private boolean suspes;
	private Date dataInici;
	private Date dataFi;
}
