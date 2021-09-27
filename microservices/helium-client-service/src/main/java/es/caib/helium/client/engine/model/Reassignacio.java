/**
 * 
 */
package es.caib.helium.client.engine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * DTO amb informació d'una reassignació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reassignacio {

	private Long id;
	private String usuariOrigen;
	private String usuariDesti;
	private Date dataInici;
	private Date dataFi;
	private Date dataCancelacio;
	private Long tipusExpedientId;

}
