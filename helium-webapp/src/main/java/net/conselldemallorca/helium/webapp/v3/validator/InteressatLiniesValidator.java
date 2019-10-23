package net.conselldemallorca.helium.webapp.v3.validator;



import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.conselldemallorca.helium.webapp.v3.command.InteressatCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;

/**
 * Constraint de validació que controla que camp email és obligatori si està habilitada l'entrega a la Direcció Electrònica Hablitada (DEH)
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InteressatLiniesValidator implements ConstraintValidator<InteressatLinies, Object> {


	@Override
	public void initialize(final InteressatLinies constraintAnnotation) {
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		boolean valid = true;
		
		try {
			
			InteressatCommand interessat = (InteressatCommand)value;
			
			if (interessat.getEntregaPostal() && (interessat.getLinia1() == null || interessat.getLinia1().isEmpty())) {
				context
					.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("interessat.form.valid.linia"))
					.addNode("linia1")
					.addConstraintViolation();
				valid = false;
			}
			if (interessat.getEntregaPostal() && (interessat.getLinia2() == null || interessat.getLinia2().isEmpty())) {
				context
					.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("interessat.form.valid.linia"))
					.addNode("linia2")
					.addConstraintViolation();
				valid = false;
			}
			if (interessat.getEntregaPostal() && (interessat.getCodiPostal() == null || interessat.getCodiPostal().isEmpty())) {
				context
					.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("interessat.form.valid.linia"))
					.addNode("codiPostal")
					.addConstraintViolation();
				valid = false;
			}
		} catch (final Exception ex) {
        	LOGGER.error("Ha d'informar els camps codi postal, línia 1 i línia 2 quan hi ha entrega postal", ex);
        }
		
		if (!valid)
			context.disableDefaultConstraintViolation();
		
		return valid;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(InteressatLiniesValidator.class);

}
