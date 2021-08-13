package es.caib.helium.client.expedient.proces.model;

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

	private String id;
	private Long expedientId;
	private String processDefinitionId;
	private String procesArrelId;
	private String procesPareId;
	private String descripcio;
	private boolean suspes;
	private Date dataInici;
	private Date dataFi;
}
