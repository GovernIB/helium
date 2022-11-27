/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.validator;

import net.conselldemallorca.helium.v3.core.api.dto.regles.EstatReglaDto;
import net.conselldemallorca.helium.v3.core.api.dto.regles.QueEnum;
import net.conselldemallorca.helium.v3.core.api.dto.regles.QuiEnum;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.command.EstatReglaCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

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
			EstatReglaDto repetit = expedientTipusService.estatReglaFindByNom(command.getEstatId(), command.getNom());
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

		return valid;
	}

}
