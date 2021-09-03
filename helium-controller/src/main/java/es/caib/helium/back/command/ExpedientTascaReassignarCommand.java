/**
 * 
 */
package es.caib.helium.back.command;

import es.caib.helium.client.engine.model.ReassignTaskData.ScriptLanguage;
import es.caib.helium.logic.intf.dto.MotorTipusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command per a la reassignació de tasques
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpedientTascaReassignarCommand {

	private String usuari;
	private String[] grups;
	private String expression;
	private ScriptLanguage expressionLanguage;
	private MotorTipusEnum motorTipus;

}
