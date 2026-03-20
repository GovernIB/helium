/**
 * 
 */
package es.caib.helium.back.command;

import org.springframework.web.multipart.MultipartFile;

import es.caib.helium.back.validator.AccioDesplegar;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command pel desplegament d'un arxiu .jar de handlers.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
@AccioDesplegar
public class ExpedientTipusAccioDesplegarCommand {
	
	/** Id de l'entorn on es desplega la definició de procés. */
	private Long entornId;
	/** Id del tipus d'expedient on es desplega la definició de procés. */
	private Long expedientTipusId;

	/** Fitxer jar amb els handlers */
	private MultipartFile file;

}
