/**
 * 
 */
package es.caib.helium.logic.intf.dto.expedient;

import es.caib.helium.logic.intf.dto.ExpedientDto.IniciadorTipusDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Classe per retornar la informació d'un expedient al iniciar-lo.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpedientIniciDto {

	private Long id;
	private String titol;
	private String numero;
	private String numeroDefault;
	private Date dataInici;
	private IniciadorTipusDto iniciadorTipus;
	private String iniciadorCodi;
	private String responsableCodi;

	private String identificador;
	private String numeroIdentificador;

	private String processInstanceId;
	private String entornCodi;


	@Override
	public String toString() {
		return "{ " +
				"id: " + getId() + ", " +
				"titol" + getTitol() + ", " +
				"numero" + getNumero() + ", " +
				"numeroDefault" + getNumeroDefault() +
				"}";
	}
}
