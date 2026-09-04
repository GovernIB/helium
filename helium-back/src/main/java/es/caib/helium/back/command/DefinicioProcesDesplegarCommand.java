/**
 *
 */
package es.caib.helium.back.command;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import es.caib.helium.back.command.DefinicioProcesDesplegarCommand.Desplegament;
import es.caib.helium.back.validator.DefinicioProcesDesplegar;

/**
 * Command pel desplegament d'un arxiu .par d'una definició de procés.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@DefinicioProcesDesplegar(groups = {Desplegament.class})
public class DefinicioProcesDesplegarCommand {

	/** Enumeració per distingir la acció a realitzar amb el desplegament JBPM. */
	public enum ACCIO_PROCES {
		// Realitza un desplegament normal
		PROCES_DESPLEGAR,
		// Sobreescriu els handlers
		PROCES_ACTUALITZAR;
	}

	/** Id de la definició de procés sobre la que es desplega la definició de procés. */
	private Long id = null;
	/** Id de l'entorn on es desplega la definició de procés. */
	private Long entornId;
	/** Id del tipus d'expedient on es desplega la definició de procés. */
	private Long expedientTipusId;
	/** Id del tipus d'expedient on es desplega la definició de procés. */
	private Long definicioProcesId;
	/** Etiqueta que s'assignarà a la nova definició de procés.*/
	private String etiqueta;
	/** Indica si el desplegament té una tasca inicial. S'haurà de validar que tingui una tasca d'usuari tot just començar el flux. */
	private boolean hasStartTask;
	/** Indica si s'iniciarà una acció massiva per actualitzar els expedients actius. */
	private boolean actualitzarExpedientsActius;
	/** Contingut del fitxer */
	@JsonIgnore
	private MultipartFile file;
	/** Indica si augmentar la versió o sobre escriure els handlers. */
	private ACCIO_PROCES accio;

	public interface Desplegament {}
}
