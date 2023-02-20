/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.conselldemallorca.helium.webapp.v3.command.DefinicioProcesDesplegarCommand.Desplegament;
import net.conselldemallorca.helium.webapp.v3.validator.AccioDesplegar;
import net.conselldemallorca.helium.webapp.v3.validator.DefinicioProcesDesplegar;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

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
