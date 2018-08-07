package net.conselldemallorca.helium.webapp.v3.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusIntegracioNotibCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;

/**
 * Validador per al manteniment de consultes del tipus d'expedient:
 * - Comprova que el codi:
 * 		- no estigui duplicat
 * 		- No comenci per majúscula seguida de minúscula
 * 		- No contingui espais
 */
public class ExpedientTipusIntegracioNotibValidator implements ConstraintValidator<ExpedientTipusIntegracioNotib, ExpedientTipusIntegracioNotibCommand>{

	@SuppressWarnings("unused")
	private String codiMissatge;

	@Override
	public void initialize(ExpedientTipusIntegracioNotib anotacio) {
		codiMissatge = anotacio.message();
	}

	@Override
	public boolean isValid(ExpedientTipusIntegracioNotibCommand command, ConstraintValidatorContext context) {
		boolean valid = true;

		if (command.isNotibActiu()) {
			
			if (command.getNotibEmisor() == null || command.getNotibEmisor().trim().isEmpty()) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("NotEmpty", null))
						.addNode("notibEmisor")
						.addConstraintViolation();
				valid = false;
//			} else if (command.getNotibEmisor().length() != 9) {
//				context.buildConstraintViolationWithTemplate(
//						MessageHelper.getInstance().getMessage("Size", new Object[]{9,9}))
//						.addNode("notibEmisor")
//						.addConstraintViolation();
//				valid = false;
			}
			
			if (command.getNotibCodiProcediment() == null || command.getNotibCodiProcediment().trim().isEmpty()) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("NotEmpty", null))
						.addNode("notibCodiProcediment")
						.addConstraintViolation();
				valid = false;
//			} else if (command.getNotibEmisor().length() != 9) {
//				context.buildConstraintViolationWithTemplate(
//						MessageHelper.getInstance().getMessage("Size.java.lang.String", new Object[]{6}))
//						.addNode("notibCodiProcediment")
//						.addConstraintViolation();
//				valid = false;
			}
			
			if (command.getNotibSeuUnitatAdministrativa() == null || command.getNotibSeuUnitatAdministrativa().trim().isEmpty()) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("NotEmpty", null))
						.addNode("notibSeuUnitatAdministrativa")
						.addConstraintViolation();
				valid = false;
			}
			
			if (command.getNotibSeuOficina() == null || command.getNotibSeuOficina().trim().isEmpty()) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("NotEmpty", null))
						.addNode("notibSeuOficina")
						.addConstraintViolation();
				valid = false;
			}
			if (command.getNotibSeuLlibre() == null || command.getNotibSeuLlibre().trim().isEmpty()) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("NotEmpty", null))
						.addNode("notibSeuLlibre")
						.addConstraintViolation();
				valid = false;
			}
			if (command.getNotibSeuOrgan() == null || command.getNotibSeuOrgan().trim().isEmpty()) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("NotEmpty", null))
						.addNode("notibSeuOrgan")
						.addConstraintViolation();
				valid = false;
			}
		}
		
		return valid;
	}

}
