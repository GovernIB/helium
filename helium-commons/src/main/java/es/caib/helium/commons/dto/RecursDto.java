/**
 *
 */
package es.caib.helium.commons.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * DTO amb informació d'una reassignació.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class RecursDto {

	private Long id;
	private String nom;
	private boolean classe;
	private boolean handler;
	private Date dataCreacio;
	private Long tipusExpedientId;
	private Long definicioProcesId;

}
