/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.helper;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.CampRegistre;
import net.conselldemallorca.helium.v3.core.api.dto.CampRegistreDto;
import net.conselldemallorca.helium.v3.core.api.service.CampService;


/**
 * Validador per als formularis dels registres
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CommonRegistreHelper implements Validator {
	@Autowired
	private CampService campService;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean supports(Class clazz) {
		return clazz.isAssignableFrom(Object.class);
	}
	public void validate(Object command, Errors errors) {
		try {
			Long registreId = (Long)PropertyUtils.getSimpleProperty(command, "registreId");
			for (CampRegistreDto campRegistre: campService.findRegistresByCampId(registreId)) {
				if (campRegistre.isObligatori())
					ValidationUtils.rejectIfEmpty(errors, campRegistre.getMembreCodi(), "not.blank");
			}
		} catch (Exception ex) {
			errors.reject("error.validator");
		}
	}

}
