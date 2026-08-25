package es.caib.helium.back.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.back.command.DefinicioProcesUpdateCommand;
import es.caib.helium.back.helper.MessageHelper;
import es.caib.helium.logic.intf.service.DefinicioProcesService;

/**
 * Validador per al manteniment de definicions de procés des de l'acció de modificar a la pipella d'informació de la definició de procés:
 * - Comprova que si es marca amb tasca inicial que realment comenci per un Start State seguida d'una tasca d'usuari.
 */
public class DefinicioProcesUpdateValidator implements ConstraintValidator<DefinicioProcesUpdate, DefinicioProcesUpdateCommand>{

	private String codiMissatge;
	@Autowired
	private DefinicioProcesService definicioProcesService;

	@Override
	public void initialize(DefinicioProcesUpdate anotacio) {
		codiMissatge = anotacio.message();
	}

	@Override
	public boolean isValid(DefinicioProcesUpdateCommand command, ConstraintValidatorContext context) {
		boolean valid = true;
		// Si es marca amb tasca inicial comprova que realment en tingui
		if (command.isHasStartTask()) {
			String startTaskName = definicioProcesService.checkTascaInicial(command.getId());
			if (startTaskName == null || startTaskName.isEmpty()) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage(this.codiMissatge + ".hasStartTask.error"))
				.addNode("hasStartTask")
				.addConstraintViolation();	
				valid = false;
			}
		}
		if (!valid)
			context.disableDefaultConstraintViolation();

		return valid;
	}

}
