/**
 * 
 */
package es.caib.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * DTO amb informació d'una reassignació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ReassignacioDto {

	private Long id;
	private String usuariOrigen;
	private String usuariDesti;
	private Date dataInici;
	private Date dataFi;
	private Date dataCancelacio;
	private Long tipusExpedientId;

}
