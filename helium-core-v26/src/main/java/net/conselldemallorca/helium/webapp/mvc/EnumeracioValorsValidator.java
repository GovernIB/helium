/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import net.conselldemallorca.helium.core.model.hibernate.EnumeracioValors;
import net.conselldemallorca.helium.core.model.service.DissenyService;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * Validador per gestionar els valors de les enumeracions
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EnumeracioValorsValidator {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean supports(Class clazz) {
		return clazz.isAssignableFrom(EnumeracioValorsCommand.class);
	}

	private DissenyService dissenyService;
	public EnumeracioValorsValidator(DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}

	public void validate(Object command, Errors errors) {
		EnumeracioValorsCommand enumeracioValorsCommand = (EnumeracioValorsCommand)command;

		ValidationUtils.rejectIfEmpty(errors, "codi", "not.blank");
		ValidationUtils.rejectIfEmpty(errors, "nom", "not.blank");

		EnumeracioValors enumeracioValors = dissenyService.findEnumeracioValorsAmbCodi(
				enumeracioValorsCommand.getEnumeracioId(),
				enumeracioValorsCommand.getCodi());
		if (enumeracioValors != null) {
			errors.rejectValue("codi", "error.enumeracio.valor.codi.repetit");
		}

	}

}