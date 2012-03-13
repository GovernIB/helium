/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.core.model.service.DissenyService;

import org.springframework.validation.Errors;

/**
 * Validador per gestionar les enumeracions
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EnumeracioValidator {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean supports(Class clazz) {
		return clazz.isAssignableFrom(EnumeracioValorsCommand.class);
	}

	private DissenyService dissenyService;
	public EnumeracioValidator(DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}

	public void validate(Object command, Errors errors) {
		Enumeracio enumeracio = (Enumeracio)command;
		Enumeracio e = dissenyService.findAmbEntornSenseTipusExpICodi(
				enumeracio.getEntorn().getId(),
				enumeracio.getCodi());
		if (e != null)
			errors.rejectValue("codi", "error.enumeracio.codi.repetit");

	}

}
