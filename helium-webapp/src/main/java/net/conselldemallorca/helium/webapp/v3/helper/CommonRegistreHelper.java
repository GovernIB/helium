/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.helper;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import es.caib.helium.logic.intf.dto.CampDto;
import es.caib.helium.logic.intf.dto.CampRegistreDto;
import es.caib.helium.logic.intf.service.CampService;


/**
 * Validador per als formularis dels registres
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CommonRegistreHelper implements Validator {

	private CampService campService;

	public CommonRegistreHelper(
			CampService campService) {
		this.campService = campService;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean supports(Class clazz) {
		return clazz.isAssignableFrom(Object.class);
	}
	public void validate(Object command, Errors errors) {
		try {
			Long registreId = (Long)PropertyUtils.getSimpleProperty(command, "registreId");
			for (CampDto camp: campService.registreFindMembresAmbRegistreId(registreId)) {
				CampRegistreDto campRegistre = campService.registreFindAmbId(camp.getId());
				if (campRegistre.isObligatori())
					ValidationUtils.rejectIfEmpty(errors, camp.getCodi(), "not.blank");
			}
		} catch (Exception ex) {
			errors.reject("error.validator");
		}
	}

}
