/**
 * 
 */
package es.caib.helium.back.helper;

import es.caib.helium.back.command.ReassignacioUsuarisCommand;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Date;

/**
 * Validador per als formularis de tasca
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ReassignacioUsuarisValidatorHelper implements Validator {
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean supports(Class clazz) {
		return clazz.isAssignableFrom(ReassignacioUsuarisCommand.class);
	}
	public void validate(Object target, Errors errors) {
		Date avui = new Date();
		ReassignacioUsuarisCommand command = (ReassignacioUsuarisCommand)target;
		ValidationUtils.rejectIfEmpty(errors, "usuariOrigen", "not.blank");
		ValidationUtils.rejectIfEmpty(errors, "usuariDesti", "not.blank");
		ValidationUtils.rejectIfEmpty(errors, "dataInici", "not.blank");
		ValidationUtils.rejectIfEmpty(errors, "dataFi", "not.blank");
		if ((command.getDataInici() != null) && (avui.compareTo(command.getDataInici()) > 0)) {
			errors.rejectValue("dataInici", "error.data.anterior");
		}
		if ((command.getDataFi() != null) && (avui.compareTo(command.getDataFi()) > 0)) {
			errors.rejectValue("dataFi", "error.data.anterior");
		}
		if ((command.getDataInici() != null) && (command.getDataFi() != null) && ((command.getDataFi()).compareTo(command.getDataInici()) < 0)) {
			errors.rejectValue("dataFi", "error.dataFi.anterior");
		}
	}
}
