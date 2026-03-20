/**
 * 
 */
package es.caib.helium.back.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.back.command.EstatReglaCommand;
import es.caib.helium.back.helper.MessageHelper;
import es.caib.helium.commons.dto.regles.AccioEnum;
import es.caib.helium.commons.dto.regles.EstatReglaDto;
import es.caib.helium.commons.dto.regles.QueEnum;
import es.caib.helium.commons.dto.regles.QuiEnum;
import es.caib.helium.logic.intf.service.ExpedientTipusService;

/**
 * Comprova que el codi d'entorn no estigui repetit.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EstatReglaValidator implements ConstraintValidator<EstatRegla, EstatReglaCommand> {

	private EstatRegla anotacio;
	@Autowired
	private ExpedientTipusService expedientTipusService;

	@Override
	public void initialize(EstatRegla anotacio) {
		this.anotacio = anotacio;
	}

	@Override
	public boolean isValid(
			EstatReglaCommand command,
			ConstraintValidatorContext context) {
		boolean valid = true;
		// comprova que el nom sigui únic
		if (command.getNom() != null) {
			EstatReglaDto repetit = expedientTipusService.estatReglaFindByNom(command.getExpedientTipusId(), command.getEstatId(), command.getNom());
			if (repetit != null && (command.getId() == null || !command.getId().equals(repetit.getId()))) {
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage(anotacio.message() + ".nom.repetit"))
						.addNode("nom")
						.addConstraintViolation();
				valid = false;
			}
		}
		// Comprova la obligatorietat dels valors
		// QUI --> Si no és tothom s'han d'informar valors
		if (QuiEnum.USUARI.equals(command.getQui()) || QuiEnum.ROL.equals(command.getQui())) {
			if (command.getQuiValor() == null || command.getQuiValor().isEmpty()) {
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage(anotacio.message() + ".qui.valor"))
						.addNode("quiValor")
						.addConstraintViolation();
				valid = false;
			}
		}
		// QUE
		if (QueEnum.AGRUPACIO.equals(command.getQue()) ||
				QueEnum.DADA.equals(command.getQue()) ||
				QueEnum.DOCUMENT.equals(command.getQue()) ||
				QueEnum.TERMINI.equals(command.getQue())) {
			if (command.getQueValor() == null || command.getQueValor().isEmpty()) {
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage(anotacio.message() + ".que.valor"))
						.addNode("queValor")
						.addConstraintViolation();
				valid = false;
			}
		}
		
		if (command.getAccio() != null &&
	            (AccioEnum.SIGNAT.equals(command.getAccio()) || AccioEnum.NOTIFICAT.equals(command.getAccio()))) {
	
			if (!(QueEnum.DOCUMENT.equals(command.getQue()) || QueEnum.DOCUMENTS.equals(command.getQue()))) {
		        context.buildConstraintViolationWithTemplate(
		                    MessageHelper.getInstance().getMessage(anotacio.message() + ".accio.document"))
		                    .addNode("que")
		                    .addConstraintViolation();
		            valid = false;
		        }
		 }

		if (!valid)
			context.disableDefaultConstraintViolation();
		return valid;
	}

}
