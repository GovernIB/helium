package es.caib.helium.validacions;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.netflix.servo.util.Strings;

import es.caib.helium.dada.domain.Dada;

public class DadaCodiValidator implements ConstraintValidator<DadaValidacio, Dada> {

	@Override
	public boolean isValid(Dada dada, ConstraintValidatorContext context) {

		var valid = true;
		var codi = dada.getCodi();
		if (Strings.isNullOrEmpty(codi) || codi.contains(".") || codi.contains(" ") || Character.isUpperCase(codi.charAt(0))) {
			valid = false;
		}
		return valid;
	}
}
